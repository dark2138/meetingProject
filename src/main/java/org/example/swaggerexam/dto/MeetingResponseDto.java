package org.example.swaggerexam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.swaggerexam.domain.Meeting;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "미팅 응답 DTO")
@Builder
public class MeetingResponseDto {

    private Long id;
    private String name;
    private String description;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private String ownerName;

    public static MeetingResponseDto convertToResponseDto(Meeting meeting) {
        return MeetingResponseDto.builder()
                .id(meeting.getId())
                .name(meeting.getTitle())
                .description(meeting.getDescription())
                .maxParticipants(meeting.getMaxParticipants())
                .ownerName(meeting.getOwner().getEmail())
                .currentParticipants(meeting.getMeetingParticipants().size())
                .build();
    }

}
