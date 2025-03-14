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
import org.example.swaggerexam.dto.ScheduleParticipantResponseDto;
import org.example.swaggerexam.dto.ScheduleRequestDto;
import org.example.swaggerexam.dto.ScheduleResponseDto;
import org.example.swaggerexam.exception.type.BadRequestException;
import org.example.swaggerexam.service.ScheduleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Schedule", description = "일정 관련 API")
@RequestMapping("/api/meetings/{meetingId}/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    // 일정 생성
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Operation(summary = "일정 생성 ", description = "일정 생성합니다.")
    @PostMapping
    public ResponseEntity<ApiResponseDto<String>> schedulesAdd(
            @Parameter(description = "JWT 인증 토큰", required = true, example = "Bearer exjflfsgkgl...")
            @RequestHeader("Authorization") String accessToken,
            @PathVariable(name = "meetingId") @Parameter(description = "미팅 ID")
            Long meetingId,
            @Valid @RequestBody @Parameter(description = "일정 생성 요청 데이터")
            ScheduleRequestDto requestDto
            ){


        try {
            accessToken = extractToken(accessToken);
            String schedule = scheduleService.createSchedule(requestDto, meetingId, accessToken);
            return ResponseEntity.ok(
                    ApiResponseDto.success("SCHEDULE_CREATE_SUCCESS", schedule)
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

    // 일정 삭제
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Operation(summary = "일정 수정 ( 생성자만 가능 )", description = "일정 수정합니다.")
    @PutMapping("/{scheduleId}")
    public ResponseEntity<ApiResponseDto<String>> schedulesUpdate(
            @Parameter(description = "JWT 인증 토큰", required = true, example = "Bearer exjflfsgkgl...")
            @RequestHeader("Authorization") String accessToken,
            @PathVariable(name = "meetingId") @Parameter(description = "미팅 ID")
            Long meetingId,
            @PathVariable(name = "scheduleId") @Parameter(description = "스케줄 ID")
            Long scheduleId,
            @Valid @RequestBody @Parameter(description = "일정 수정 요청 데이터")
            ScheduleRequestDto requestDto
    ){


        try {
            accessToken = extractToken(accessToken);
            String schedule = scheduleService.upadteSchedule(requestDto, meetingId, scheduleId, accessToken);
            return ResponseEntity.ok(
                    ApiResponseDto.success("SCHEDULE_CREATE_SUCCESS", schedule)
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

    // 일정 삭제
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Operation(summary = "일정 삭제 ( 생성자만 가능 )", description = "일정 삭제합니다.")
    @DeleteMapping("/{scheduleId}/delete")
    public ResponseEntity<ApiResponseDto<String>> schedulesDelete(
            @Parameter(description = "JWT 인증 토큰", required = true, example = "Bearer exjflfsgkgl...")
            @RequestHeader("Authorization") String accessToken,
            @PathVariable(name = "meetingId") @Parameter(description = "미팅 ID")
            Long meetingId,
            @PathVariable(name = "scheduleId") @Parameter(description = "스케줄 ID")
            Long scheduleId
    ){


        try {

            accessToken = extractToken(accessToken);
            String result = scheduleService.deleteSchedule(meetingId, scheduleId, accessToken);
            return ResponseEntity.ok(
                    ApiResponseDto.success("SCHEDULE_DELETE_SUCCESS", result)
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
    

    // 일정 목록 조회
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Operation(summary = "일정 목록 조회 ", description = "일정 목록 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponseDto<List<ScheduleResponseDto>>> getSchedules(
            @PathVariable(name = "meetingId") @Parameter(description = "미팅 ID")
            Long meetingId){

        try {
            List<ScheduleResponseDto> schedules = scheduleService.list(meetingId);
            return ResponseEntity.ok(
                    ApiResponseDto.success("SCHEDULE_LIST_SUCCESS", schedules)
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

    // 일정 참가
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Operation(summary = "일정 참가 ", description = "일정 참가합니다.")
    @PostMapping("/{scheduleId}/join")
    public ResponseEntity<ApiResponseDto<String>> schedulesJoin(
            @Parameter(description = "JWT 인증 토큰", required = true, example = "Bearer exjflfsgkgl...")
            @RequestHeader("Authorization") String accessToken,
            @PathVariable(name = "meetingId") @Parameter(description = "미팅 ID")
            Long meetingId,
            @PathVariable(name = "scheduleId") @Parameter(description = "스케줄 ID")
            Long scheduleId
    ){


        try {

            accessToken = extractToken(accessToken);
            String result = scheduleService.scheduleJoin(meetingId, scheduleId, accessToken);
            return ResponseEntity.ok(
                    ApiResponseDto.success("SCHEDULE_LIST_SUCCESS", result)
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

    // 일정 탈퇴
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Operation(summary = "일정 참가자 탈퇴 ", description = "일정 참가자 탈퇴합니다.")
    @DeleteMapping("/{scheduleId}/leave")
    public ResponseEntity<ApiResponseDto<String>> schedulesLeave(
            @Parameter(description = "JWT 인증 토큰", required = true, example = "Bearer exjflfsgkgl...")
            @RequestHeader("Authorization") String accessToken,
            @PathVariable(name = "meetingId") @Parameter(description = "미팅 ID")
            Long meetingId,
            @PathVariable(name = "scheduleId") @Parameter(description = "스케줄 ID")
            Long scheduleId
    ){


        try {

            accessToken = extractToken(accessToken);
            String result = scheduleService.leaveSchedule(meetingId, scheduleId, accessToken);
            return ResponseEntity.ok(
                    ApiResponseDto.success("SCHEDULEPARTICIPANT_DELETE_SUCCESS", result)
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


    // 특정 일정 참가자 목록 조회
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Operation(summary = "특정 일정 참가자 목록 조회 ", description = "특정 일정 참가자 목록 조회랍니다.")
    @GetMapping("/{scheduleId}/participants")
    public ResponseEntity<ApiResponseDto<List<ScheduleParticipantResponseDto>>> schedulesGetParticipants(
            @PathVariable(name = "meetingId") @Parameter(description = "미팅 ID")
            Long meetingId,
            @PathVariable(name = "scheduleId") @Parameter(description = "스케줄 ID")
            Long scheduleId
    ){
        try {
            List<ScheduleParticipantResponseDto> participants =
                    scheduleService.getScheduleParticipants(meetingId, scheduleId);
            return ResponseEntity.ok(
                    ApiResponseDto.success("SCHEDULEPARTICIPANT_LIST_SUCCESS", participants)
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
