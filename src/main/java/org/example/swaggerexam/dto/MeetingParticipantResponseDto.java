package org.example.swaggerexam.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.swaggerexam.domain.Meeting;
import org.example.swaggerexam.domain.MeetingParticipant;
import org.example.swaggerexam.domain.User;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetingParticipantResponseDto {

    private Long id;
    private String email;
    private String status;
    private String role;

    public static MeetingParticipantResponseDto fromEntity(MeetingParticipant participant) {
        User user = participant.getUser();
        return MeetingParticipantResponseDto.builder()
                .id(participant.getId())
                .email(user.getEmail())
                .status(participant.getStatus().name()) // enum 이름을 문자열로 변환
                .role(participant.getRole().name())
                .build();
    }


}
