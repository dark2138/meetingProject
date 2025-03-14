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
@Schema(description = "미팅 정보 DTO")
@Builder
public class MeetingInfoDto {

    private Long id;
    private String title;

    public static MeetingInfoDto fromEntity(Meeting meeting) {
       return   MeetingInfoDto.builder()
                .id(meeting.getId())
                .title(meeting.getTitle())
                .build();
    }
}
