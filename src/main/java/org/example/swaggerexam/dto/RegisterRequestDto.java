package org.example.swaggerexam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@Schema(description = "회원가입 요청 DTO")
public class RegisterRequestDto {

    @Schema(description = "사용자 이메일" , example = "user@example.com")
    private String email;
    @Schema(description = "사용자 비밀번호", example = "$22!@#ff")
    private String password;


}
