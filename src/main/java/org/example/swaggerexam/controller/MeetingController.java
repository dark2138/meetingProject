package org.example.swaggerexam.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.example.swaggerexam.dto.ApiResponseDto;
import org.example.swaggerexam.dto.MeetingParticipantResponseDto;
import org.example.swaggerexam.dto.MeetingRequestDto;
import org.example.swaggerexam.dto.MeetingResponseDto;
import org.example.swaggerexam.exception.type.BadRequestException;
import org.example.swaggerexam.service.MeetingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Meetings", description = "모임 관련 API")
@RequestMapping("/api/meetings")
public class MeetingController {


    private final MeetingService meetingService;


    @Operation(summary = "모임 생성 ", description = "모임을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping
    public ResponseEntity<ApiResponseDto<MeetingResponseDto>> create(
            @Parameter(description = "JWT 인증 토큰", required = true, example = "Bearer exjflfsgkgl...")
            @RequestHeader(value = "Authorization") String accessToken,
            @Valid @RequestBody @Parameter(description = "미팅 생성 요청 데이터")
            MeetingRequestDto meetingRequestDto) {


        try {

            accessToken = extractToken(accessToken);
            MeetingResponseDto add = meetingService.add(meetingRequestDto, accessToken);


            return ResponseEntity.ok(
                    ApiResponseDto.success("MEETING_CREATE_SUCCESS", add)
            );

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

    @Operation(summary = "모든 모임 목록 조회", description = "모든 모임 정보를 목록 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping
    public ResponseEntity<ApiResponseDto<List<MeetingResponseDto>>> list() {
        try {

            List<MeetingResponseDto> list = meetingService.list();

            return ResponseEntity.ok(
                    ApiResponseDto.success("MEETING_LIST_SUCCESS", list)
            );
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

    @Operation(summary = "모임 수정 ( 생성자만 가능 ) ", description = "생성자의 고유 ID를 이용하여 모임을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PutMapping("/{meetingId}")
    public ResponseEntity<ApiResponseDto<String>> modifyMeeting(
            @Parameter(description = "JWT 인증 토큰", required = true, example = "Bearer exjflfsgkgl...")
            @RequestHeader("Authorization") String accessToken,
            @PathVariable(name = "meetingId") @Parameter(description = "미팅 ID")
            Long meetingId,
            @Valid @RequestBody @Parameter(description = "미팅 수정 요청 데이터")
            MeetingRequestDto meetingRequestDto
    ) {
        try {
            // 1. 토큰에서 "Bearer " 제거 (유틸리티 메서드 추천)
            String token = extractToken(accessToken);

            // 2. 서비스 호출
            String result = meetingService.modify(meetingRequestDto, meetingId, token);

            // 3. 표준화된 응답 반환
            return ResponseEntity.ok(
                    ApiResponseDto.success("MEETING_MODIFY_SUCCESS", result)
            );

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

    @Operation(summary = "모임 삭제 (생성자만 가능) ", description = "생성자의 고유 ID를 이용하여 모임을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping("/{meetingId}")
    public ResponseEntity<ApiResponseDto<String>> delete(
            @PathVariable(name = "meetingId") @Parameter(description = "미팅 ID")
            Long meetingId,
            @Parameter(description = "JWT 인증 토큰", required = true, example = "Bearer exjflfsgkgl...")
            @RequestHeader("Authorization") String accessToken) {


        try {
            String result = meetingService.delete(meetingId, accessToken);

            return ResponseEntity.ok(
                    ApiResponseDto.success("MEETING_DELETE_SUCCESS", result)
            );

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

    @Operation(summary = "특정 모임 참가 ", description = "특정 모임을 참가합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/{meetingId}/join")
    public ResponseEntity<ApiResponseDto<String>> joinMeeting(
            @PathVariable(name = "meetingId") @Parameter(description = "미팅 ID")
            Long meetingId,
            @Parameter(description = "JWT 인증 토큰", required = true, example = "Bearer exjflfsgkgl...")
            @RequestHeader("Authorization") String accessToken) {

        try {

            accessToken = extractToken(accessToken);
            String result = meetingService.meetingJoin(meetingId, accessToken);
            return ResponseEntity.ok(
                    ApiResponseDto.success("MEETING_JOIN_SUCCESS", result)
            );
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

    @Operation(summary = "특정 모임 모든 참가자 목록 조회", description = "특정 모임 모든 참가자 목록 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/{meetingId}/participants")
    public ResponseEntity<ApiResponseDto<List<MeetingParticipantResponseDto>>> getParticipants(
            @PathVariable("meetingId") @Parameter(description = "미팅 ID")
            Long meetingId) {

        try {
            List<MeetingParticipantResponseDto> participants =
                    meetingService.getParticipants(meetingId);
            return ResponseEntity.ok(
                    ApiResponseDto.success("MEETINGPARTICIPANT_LIST_SUCCESS", participants)
            );
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

    @Operation(summary = "특정 모임 참가자 참가 취소 ", description = "특정 모임 참가자 참가 취소합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping("/participants/{participantId}")
    public ResponseEntity<?> deleteParticipant(
            @PathVariable(name = "participantId") @Parameter(description = "미팅 참가자 ID")
            Long participantId,
            @Parameter(description = "JWT 인증 토큰", required = true, example = "Bearer exjflfsgkgl...")
            @RequestHeader("Authorization") String accessToken) {


        try {

            accessToken = extractToken(accessToken);
            String result = meetingService.deleteParticipants(participantId, accessToken);
            return ResponseEntity.ok(
                    ApiResponseDto.success("MEETINGPARTICIPANT_DELETE_SUCCESS", result)
            );
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


    // Bearer 토큰 추출 유틸리티 메서드
    private String extractToken(String header) {
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        throw new IllegalArgumentException("Invalid token format");
    }


}
