package org.example.swaggerexam.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.example.swaggerexam.domain.*;
import org.example.swaggerexam.dto.*;
import org.example.swaggerexam.exception.type.ForbiddenException;
import org.example.swaggerexam.jwt.utill.JwtUtil;
import org.example.swaggerexam.repository.*;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleService {
    private  final MeetingParticipantRepository meetingParticipantRepository;
    private final ScheduleParticipantRepository scheduleParticipantRepository;
    private final MeetingRepository meetingRepository;
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    public String createSchedule(ScheduleRequestDto scheduleRequestDto,
                                              Long meetingId,
                                              String accessToken) {

        String email = jwtUtil.validateAccessToken(accessToken);
        if (email == null) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        // 2. 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없음"));


        Schedule schedule = new Schedule();
        schedule.setTitle(scheduleRequestDto.getTitle());
        schedule.setDate(String.valueOf(scheduleRequestDto.getDate()));
        schedule.setTime(String.valueOf(scheduleRequestDto.getTime()));
        schedule.setLocation(scheduleRequestDto.getLocation());


        schedule.setMeeting(meetingRepository.findById(meetingId).get());
        schedule.setOwner(userRepository.findByEmail(email).get());
        scheduleRepository.save(schedule);

        return "스케줄 생성 성공";

    }


    @Transactional
    public String upadteSchedule(ScheduleRequestDto scheduleRequestDto,
                                 Long meetingId,
                                 Long scheduleId,
                                 String accessToken) {

        String email = jwtUtil.validateAccessToken(accessToken);
        if (email == null) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수 없습니다."));


        validateMeetingAssociation(schedule, meetingId);
        validateOwnership(user, schedule);
        validate(scheduleRequestDto);


        schedule.setTitle(scheduleRequestDto.getTitle());
        schedule.setDate(scheduleRequestDto.getDate());
        schedule.setTime(scheduleRequestDto.getTime());
        schedule.setLocation(scheduleRequestDto.getLocation());

        return "일정 수정 성공";

    }


    @Transactional
    public String deleteSchedule(Long meetingId, Long scheduleId, String accessToken) {
        // 1. 토큰 검증 및 이메일 추출
        String email = jwtUtil.validateAccessToken(accessToken);
        if (email == null) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        // 2. 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 3. 일정 조회
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수 없습니다."));

        // 4. 미팅 소속 확인
        if (!schedule.getMeeting().getId().equals(meetingId)) {
            throw new IllegalArgumentException("해당 미팅의 일정이 아닙니다.");
        }

        // 5. 권한 확인 (소유자 또는 관리자)
        if (!user.getId().equals(schedule.getOwner().getId())) {
            throw new IllegalStateException("일정 삭제 권한이 없습니다.");
        }

        // 6. 일정 삭제
        scheduleRepository.delete(schedule);

        return "일정이 성공적으로 삭제되었습니다.";
    }






    @Transactional(readOnly = true)
    public List<ScheduleResponseDto> list(Long meetingId) {

        // 1. 미팅 존재 여부 확인 (선택 사항)
        if (!meetingRepository.existsById(meetingId)) {
            throw new IllegalArgumentException("미팅이 존재하지 않습니다.");
        }

        // 2. 스케줄 조회 (페치 조인으로 N+1 문제 해결)
        List<Schedule> schedules = scheduleRepository.findByMeetingIdWithDetails(meetingId);


        return schedules.stream()
                .map(ScheduleResponseDto::convertToResponseDto)
                .collect(Collectors.toList());

    }



    @Transactional
    public String scheduleJoin( Long meetingId,  Long scheduleId , String accessToken) {

        // 1. JWT 토큰 유효성 검사
        String email = jwtUtil.validateAccessToken(accessToken);
        if (email == null) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        // 2. 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없음"));

        // 3. 미팅 존재 여부 확인 (Optional 처리)
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("미팅이 존재하지 않습니다."));

        // 4. 스케줄 존재 여부 및 미팅 소속 확인
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("일정이 존재하지 않습니다."));

        if (!schedule.getMeeting().getId().equals(meetingId)) {
            throw new IllegalArgumentException("해당 미팅의 스케줄이 아닙니다.");
        }

        // 5. 미팅 참가자 여부 확인 (필요시)
        if (!meetingParticipantRepository.existsByMeetingAndUser(meeting, user)) {
            throw new IllegalStateException("미팅 참가자만 스케줄에 참여할 수 있습니다.");
        }

        // 6. 스케줄 중복 참가 확인
        if (scheduleParticipantRepository.existsByScheduleAndUser(schedule, user)) {
            throw new IllegalStateException("이미 참가한 일정입니다.");
        }

        // 7. 미팅 참가자 수 제한 확인
        int currentParticipants = scheduleParticipantRepository.countBySchedule(schedule);
        if (currentParticipants >= meeting.getMaxParticipants()) {
            throw new IllegalStateException("미팅 참가자 수 제한을 초과했습니다.");
        }

        // 7. 스케줄 참가자 생성 및 저장
        ScheduleParticipant participant = ScheduleParticipant.builder()
                .schedule(schedule)
                .user(user)
                .status(ScheduleParticipant.Status.ATTENDING) // 기본값 설정
                .build();

        scheduleParticipantRepository.save(participant);

        return "스케줄 참가 성공";
    }


    @Transactional
    public String leaveSchedule(Long meetingId, Long scheduleId, String accessToken) {
        // 1. JWT에서 이메일 추출
        String email = jwtUtil.validateAccessToken(accessToken);
        if (email == null) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        // 2. 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없음"));

        // 3. 미팅 및 스케줄 소속 확인
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("일정이 존재하지 않습니다."));

        if (!schedule.getMeeting().getId().equals(meetingId)) {
            throw new IllegalArgumentException("해당 미팅의 일정이 아닙니다.");
        }

        // 4. 참가자 여부 확인
        ScheduleParticipant participant = scheduleParticipantRepository.findByScheduleAndUser(schedule, user)
                .orElseThrow(() -> new IllegalStateException("참가한 일정이 아닙니다."));

        // 5. 참가 기록 삭제
        scheduleParticipantRepository.delete(participant);

        return "일정 탈퇴 성공";
    }

    @Transactional(readOnly = true)
    public List<ScheduleParticipantResponseDto> getScheduleParticipants(Long meetingId, Long scheduleId) {
        // 1. 미팅 및 스케줄 소속 확인
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("일정이 존재하지 않습니다."));

        if (!schedule.getMeeting().getId().equals(meetingId)) {
            throw new IllegalArgumentException("해당 미팅의 일정이 아닙니다.");
        }

        // 2. 참가자 조회 (페치 조인으로 최적화)
        List<ScheduleParticipant> participants =
                scheduleParticipantRepository.findByScheduleWithUser(schedule);

        // 3. DTO 변환
        return participants.stream()
                .map(participant -> ScheduleParticipantResponseDto.fromEntity(participant.getUser()))
                .collect(Collectors.toList());
    }




    private void validateMeetingAssociation(Schedule schedule, Long meetingId) {
        if (!schedule.getMeeting().getId().equals(meetingId)) {
            throw new IllegalArgumentException("미팅과 일정이 매칭되지 않습니다.");
        }
    }

    private void validateOwnership(User user, Schedule schedule) {
        if (!user.getId().equals(schedule.getOwner().getId())) {
            throw new ForbiddenException("일정 수정 권한이 없습니다.");
        }
    }



    public void validate(ScheduleRequestDto scheduleRequestDto) {
        if (scheduleRequestDto.getTitle() == null || scheduleRequestDto.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }

        if (scheduleRequestDto.getLocation() == null || scheduleRequestDto.getLocation().isEmpty()) {
            throw new IllegalArgumentException("Location cannot be null or empty");
        }

        // 날짜, 시간 형식 등 유효성 검사
        if (scheduleRequestDto.getDate() == null || scheduleRequestDto.getTime() == null) {
            throw new IllegalArgumentException("날짜와 시간은 필수 입력 항목입니다.");
        }

    }
}
