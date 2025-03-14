package org.example.swaggerexam.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.swaggerexam.dto.*;
import org.example.swaggerexam.exception.type.BadRequestException;
import org.example.swaggerexam.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "인증 관련 API")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;


    @Operation(
            summary = "회원가입",
            description = "이메일과 비밀번호를 입력하여 회원가입을 합니다",
            tags = {"Authentication"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/register")
    public ResponseEntity<ApiResponseDto<String>> register(
            @Valid @RequestBody @Parameter(description = "사용자 생성 요청 데이터")
            RegisterRequestDto registerRequestDto) {

        try {
            // 회원가입
            String register = userService.register(registerRequestDto);


            return ResponseEntity.ok(ApiResponseDto.success("Register_successfully", register));

        } catch (IllegalArgumentException e) {
            log.error("Invalid token: ", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponseDto.error("INVALID_TOKEN", e.getMessage()));
        } catch (IllegalStateException e) {
            log.error("Permission denied: ", e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponseDto.error("PERMISSION_DENIED", e.getMessage()));
        } catch (BadRequestException e) {
            log.error("Bad request: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDto.error("BAD_REQUEST", e.getMessage()));
        } catch (Exception e) {
            log.error("Internal server error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("INTERNAL_SERVER_ERROR", "An unexpected error occurred"));
        }

    }

    @Operation(
            summary = "로그인",
            description = "이메일과 비밀번호를 입력하여 로그인을 합니다"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponseDto<LoginResponseDto>> login(
            @Valid @RequestBody @Parameter(description = "로그인 생성 요청 데이터")
            LoginRequestDto loginRequestDto) {

        try {
            // 로그인이 수행될때 할일 구현
            LoginResponseDto response = userService.login(loginRequestDto);

            return ResponseEntity.ok(ApiResponseDto.success("Login successfully", response));

        } catch (IllegalArgumentException e) {
            log.error("Invalid token: ", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponseDto.error("INVALID_TOKEN", e.getMessage()));
        } catch (IllegalStateException e) {
            log.error("Permission denied: ", e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponseDto.error("PERMISSION_DENIED", e.getMessage()));
        } catch (BadRequestException e) {
            log.error("Bad request: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDto.error("BAD_REQUEST", e.getMessage()));
        } catch (Exception e) {
            log.error("Internal server error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("INTERNAL_SERVER_ERROR", "An unexpected error occurred"));
        }


    }

    @Operation(summary = "사용자 로그아웃", description = "사용자 로그아웃합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/logout")
    public ResponseEntity<ApiResponseDto<String>> logout(
            @Parameter(description = "JWT 인증 토큰", required = true, example = "Bearer exjflfsgkgl...")
            @RequestHeader(value = "Authorization") String accessToken,
            @RequestParam(value = "refreshToken", required = false)
            String refreshToken
    ) {
        try {
            if (accessToken.startsWith("Bearer ")) {
                accessToken = accessToken.substring(7); // "Bearer " 제거
            }

            String result = userService.logout(accessToken, refreshToken);

            return ResponseEntity.ok(ApiResponseDto.success("Logged out successfully", result));
        } catch (IllegalArgumentException e) {
            log.error("Invalid token: ", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponseDto.error("INVALID_TOKEN", e.getMessage()));
        } catch (IllegalStateException e) {
            log.error("Permission denied: ", e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponseDto.error("PERMISSION_DENIED", e.getMessage()));
        } catch (BadRequestException e) {
            log.error("Bad request: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDto.error("BAD_REQUEST", e.getMessage()));
        } catch (Exception e) {
            log.error("Internal server error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("INTERNAL_SERVER_ERROR", "An unexpected error occurred"));
        }

    }

    @Operation(summary = "사용자 목록 조회", description = "사용자 목록을 페이지 단위로 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/users")
    public ResponseEntity<ApiResponseDto<Page<UserDto>>> getUsers(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        try {

            Page<UserDto> users = userService.getUsers(page - 1, size);

            return ResponseEntity.ok(ApiResponseDto.success("USERS_LIST_SUCCESS", users));
        } catch (IllegalArgumentException e) {
            log.error("Invalid token: ", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponseDto.error("INVALID_TOKEN", e.getMessage()));
        } catch (IllegalStateException e) {
            log.error("Permission denied: ", e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponseDto.error("PERMISSION_DENIED", e.getMessage()));
        } catch (BadRequestException e) {
            log.error("Bad request: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDto.error("BAD_REQUEST", e.getMessage()));
        } catch (Exception e) {
            log.error("Internal server error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("INTERNAL_SERVER_ERROR", "An unexpected error occurred"));
        }


    }

    @Operation(summary = "사용자 정보 조회", description = "사용자의 고유 ID를 이용하여 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponseDto<UserDto>> getUserById(
            @PathVariable("id") @Parameter(description = "사용자 ID")
            Long id) {
        try {

            UserDto userDto = userService.getUser(id);

            return ResponseEntity.ok(ApiResponseDto.success("USER_LIST_SUCCESS", userDto));
        } catch (IllegalArgumentException e) {
            log.error("Invalid token: ", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponseDto.error("INVALID_TOKEN", e.getMessage()));
        } catch (IllegalStateException e) {
            log.error("Permission denied: ", e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponseDto.error("PERMISSION_DENIED", e.getMessage()));
        } catch (BadRequestException e) {
            log.error("Bad request: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDto.error("BAD_REQUEST", e.getMessage()));
        } catch (Exception e) {
            log.error("Internal server error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("INTERNAL_SERVER_ERROR", "An unexpected error occurred"));
        }
    }


}
