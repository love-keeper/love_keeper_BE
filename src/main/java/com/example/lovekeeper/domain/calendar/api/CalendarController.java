package com.example.lovekeeper.domain.calendar.api;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.lovekeeper.domain.calendar.dto.response.CalendarResponse;
import com.example.lovekeeper.domain.calendar.service.query.CalendarQueryService;
import com.example.lovekeeper.global.common.BaseResponse;
import com.example.lovekeeper.global.security.user.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
public class CalendarController {

	private final CalendarQueryService calendarQueryService;

	@GetMapping
	public BaseResponse<CalendarResponse> getCalendarData(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam int year,
		@RequestParam int month
	) {
		return BaseResponse.onSuccess(
			calendarQueryService.getCalendarDataForMember(userDetails.getMember().getId(), year, month));
	}

}