package com.example.lovekeeper.domain.calendar.api;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.lovekeeper.domain.calendar.dto.response.CalendarResponse;
import com.example.lovekeeper.domain.calendar.service.query.CalendarQueryService;
import com.example.lovekeeper.global.common.BaseResponse;
import com.example.lovekeeper.global.security.user.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@Tag(name = "캘린더", description = "사용자의 캘린더 데이터를 조회하는 API")
@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
@Validated
public class CalendarController {

	private final CalendarQueryService calendarQueryService;

	@Operation(summary = "캘린더 데이터 조회",
		description = "인증된 사용자의 특정 연도와 월(선택적으로 일)에 해당하는 캘린더 데이터를 조회합니다."
		, security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "캘린더 데이터 조회 성공",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터 (연도, 월, 일 형식 오류)",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "404", description = "캘린더 데이터가 존재하지 않음",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class)))
	})
	@GetMapping
	public BaseResponse<CalendarResponse> getCalendarDataByYearAndMonth(
		@Parameter(hidden = true) // AuthenticationPrincipal은 Swagger UI에서 보이지 않도록 설정
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@Parameter(description = "조회할 연도 (예: 2023)", required = true, example = "2023")
		@RequestParam @Min(value = 1900, message = "연도는 1900 이상이어야 합니다.") int year,
		@Parameter(description = "조회할 월 (1~12)", required = true, example = "10")
		@RequestParam @Min(1) @Max(12) int month,
		@Parameter(description = "조회할 일 (선택 사항, 1~31)", required = false, example = "15")
		@RequestParam(required = false) @Min(1) @Max(31) Integer day) {
		return BaseResponse.onSuccess(
			calendarQueryService.getCalendarDataForMember(userDetails.getMember().getId(), year, month, day));
	}
}