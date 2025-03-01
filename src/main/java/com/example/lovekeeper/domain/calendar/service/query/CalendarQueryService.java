// CalendarQueryService 인터페이스
package com.example.lovekeeper.domain.calendar.service.query;

import com.example.lovekeeper.domain.calendar.dto.response.CalendarResponse;

public interface CalendarQueryService {
	CalendarResponse getCalendarDataForMember(Long memberId, int year, int month, Integer day);
}