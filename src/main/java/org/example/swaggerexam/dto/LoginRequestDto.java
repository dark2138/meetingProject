package org.example.swaggerexam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "로그인 요청 DTO")
public class LoginRequestDto {
    @Schema(description = "사용자 email" , example = "kk123@example.com")
    private String email;

    @Schema(description = "사용자 password" , example = "#D1!@3")
    private String password;
}
