package org.example.swaggerexam.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.swaggerexam.domain.Meeting;
import org.example.swaggerexam.domain.Schedule;
import org.example.swaggerexam.domain.ScheduleParticipant;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "스케줄 응답 DTO")
@Builder
public class ScheduleResponseDto {

    private Long id;
    private String title;

    /*
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime time;
*/
    private String date;

    private String time;


    private String location;
    private MeetingInfoDto meeting;
    private List<ScheduleParticipantResponseDto> participants;


    public static ScheduleResponseDto convertToResponseDto(Schedule schedule) {
        return ScheduleResponseDto.builder()
                .id(schedule.getId())
                .title(schedule.getTitle())
                .date(schedule.getDate())
                .time(schedule.getTime())
                .location(schedule.getLocation())
                .meeting(MeetingInfoDto.fromEntity(schedule.getMeeting()))
                .participants(schedule.getScheduleParticipants().stream()
                        .map(sp ->
                                        ScheduleParticipantResponseDto.fromEntity(sp.getUser())
                                )
                        .collect(Collectors.toList())
                )
                .build();
    }








}
