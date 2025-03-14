package org.example.swaggerexam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.swaggerexam.domain.ScheduleParticipant;
import org.example.swaggerexam.domain.User;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "스케줄 참여자 응답 DTO")
@Builder
public class ScheduleParticipantResponseDto {

    private Long id;
    private String email;

    public static ScheduleParticipantResponseDto fromEntity(User user) {
        return ScheduleParticipantResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .build();
    }
}
