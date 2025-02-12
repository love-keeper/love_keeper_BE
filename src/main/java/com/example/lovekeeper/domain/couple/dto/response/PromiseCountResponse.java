package com.example.lovekeeper.domain.couple.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PromiseCountResponse {

	private int year;
	private int month;
	private long promiseCount;

}
