package com.example.lovekeeper.domain.calendar.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DateCountResponse {
	private LocalDate date;
	private long count;
	
	// 기존 생성자
	public DateCountResponse(LocalDate date, long count) {
		this.date = date;
		this.count = count;
	}
	
	// Letter 엔티티에서 사용할 생성자 - LocalDateTime을 LocalDate로 변환
	public DateCountResponse(LocalDateTime dateTime, Long count) {
		this.date = dateTime != null ? dateTime.toLocalDate() : null;
		this.count = count != null ? count : 0;
	}
}
