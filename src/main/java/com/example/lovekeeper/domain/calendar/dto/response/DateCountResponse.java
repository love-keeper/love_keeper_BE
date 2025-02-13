package com.example.lovekeeper.domain.calendar.dto.response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DateCountResponse {
	private LocalDate date;
	private long count;
}
