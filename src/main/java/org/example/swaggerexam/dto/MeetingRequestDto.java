package org.example.swaggerexam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "미팅 요청 DTO")
public class MeetingRequestDto {

    private String name;
    private String description;
    private Integer maxParticipants;

}
