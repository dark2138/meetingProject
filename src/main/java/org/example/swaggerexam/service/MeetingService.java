package org.example.swaggerexam.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.swaggerexam.domain.Meeting;
import org.example.swaggerexam.domain.MeetingParticipant;
import org.example.swaggerexam.domain.User;
import org.example.swaggerexam.dto.MeetingParticipantResponseDto;
import org.example.swaggerexam.dto.MeetingRequestDto;
import org.example.swaggerexam.dto.MeetingResponseDto;
import org.example.swaggerexam.jwt.utill.JwtUtil;
import org.example.swaggerexam.repository.MeetingParticipantRepository;
import org.example.swaggerexam.repository.MeetingRepository;
import org.example.swaggerexam.repository.ScheduleParticipantRepository;
import org.example.swaggerexam.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingParticipantRepository meetingParticipantRepository;
    private final MeetingRepository meetingRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;


    @Transactional
    public MeetingResponseDto add(MeetingRequestDto meetingRequestDto, String accessToken) {
        String email = jwtUtil.validateAccessToken(accessToken);
        if (email == null) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        validate(meetingRequestDto);

        Meeting meeting = new Meeting();
        meeting.setTitle(meetingRequestDto.getName());
        meeting.setDescription(meetingRequestDto.getDescription());
        meeting.setMaxParticipants(meetingRequestDto.getMaxParticipants());


        User user =  userRepository.findByEmail(email).get();

        meeting.setOwner(user);

        return MeetingResponseDto.convertToResponseDto(meetingRepository.save(meeting));
    }

    @Transactional
    public List<MeetingResponseDto> list() {

        List<Meeting> all = meetingRepository.findAll();


        return all.stream()
                .map(MeetingResponseDto::convertToResponseDto)
                .collect(Collectors.toList());

    }


    @Transactional
    public String modify(MeetingRequestDto meetingRequestDto, Long meetingId,  String accessToken) {

        // 1. JWT에서 이메일 추출
        String email = jwtUtil.validateAccessToken(accessToken);
        if (email == null) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        // 2. 기존 미팅 조회
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("미팅이 존재하지 않습니다."));

        // 3. Owner 검증
        User owner = meeting.getOwner();
        if (!owner.getEmail().equals(email)) {
            throw new IllegalArgumentException("생성자만 수정할 수 있습니다.");
        }

        if (meetingRequestDto.getMaxParticipants() < meeting.getMeetingParticipants().size()) {
            throw new IllegalArgumentException("최대 인원은 현재 참가자 수보다 적을 수 없습니다.");
        }

        // 4. 필드 업데이트
        meeting.setTitle(meetingRequestDto.getName());
        meeting.setDescription(meetingRequestDto.getDescription());
        meeting.setMaxParticipants(meetingRequestDto.getMaxParticipants());


        return "수정 성공";
    }


    @Transactional
    public String delete(Long meetingId, String accessToken) {
        String email = jwtUtil.validateAccessToken(accessToken);
        if (email == null) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("미팅이 존재하지 않습니다."));


        User owner = meeting.getOwner();
        if (!owner.getEmail().equals(email)) {
            throw new IllegalArgumentException("생성자만 삭제 할 수 있습니다.");
        }

        meetingRepository.deleteById(meeting.getId());

        return "미팅 삭제 성공";
    }

    @Transactional
    public String meetingJoin( Long meetingId, String accessToken) {

        MeetingParticipant.Role participantRole;


        // 1. JWT 토큰에서 사용자 이메일 추출
        String email = jwtUtil.validateAccessToken(accessToken);
        if (email == null) {
            throw new IllegalArgumentException("유효하지 않은 토큰");
        }

        // 2. 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없음"));

        // 3. 미팅 조회
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("미팅을 찾을 수 없음"));


        if (meeting.getOwner().getEmail().equals(user.getEmail())) {
            participantRole = MeetingParticipant.Role.OWNER;
        } else {
            participantRole = MeetingParticipant.Role.PARTICIPANT;
        }


        // 4. 이미 참가한 사용자인지 확인
        if (meetingParticipantRepository.existsByMeetingAndUser(meeting, user)) {
            throw new IllegalStateException("이미 참가한 미팅");
        }

        // 5. 최대 인원 확인
        int currentParticipants = meeting.getMeetingParticipants().size();
        if (currentParticipants >= meeting.getMaxParticipants()) {
            throw new IllegalStateException("모임 정원 초과");
        }

        // 6. 참가자 추가
        MeetingParticipant participant = MeetingParticipant.builder()
                .meeting(meeting)
                .user(user)
                .status(MeetingParticipant.Status.ATTENDING)
                .role(participantRole)
                .build();

        meetingParticipantRepository.save(participant);

        return "참가 성공";
    }


    @Transactional
    public List<MeetingParticipantResponseDto> getParticipants(Long meetingId) {
        List<MeetingParticipant> participants = meetingParticipantRepository.findByMeetingId(meetingId);
        return participants.stream()
                .map(MeetingParticipantResponseDto::fromEntity)
                .collect(Collectors.toList());
    }


    @Transactional
    public String deleteParticipants(Long meetingPartId, String accessToken) {

        String email = jwtUtil.validateAccessToken(accessToken);
        if (email == null) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        MeetingParticipant meetingParticipant = meetingParticipantRepository.findById(meetingPartId)
                .orElseThrow(() -> new IllegalArgumentException("미팅 참여자를 찾을 수 없음"));


        meetingParticipantRepository.delete(meetingParticipant);


        return "참가 취소 성공";
    }







    public void validate(MeetingRequestDto meetingRequestDto) {
        if (meetingRequestDto.getName() == null || meetingRequestDto.getName().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }

        if (meetingRequestDto.getMaxParticipants() == null || meetingRequestDto.getMaxParticipants() <= 0) {
            throw new IllegalArgumentException("Max participants must be greater than 0");
        }
    }


}
