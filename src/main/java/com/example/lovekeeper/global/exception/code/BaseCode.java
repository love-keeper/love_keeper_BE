package com.example.lovekeeper.global.exception.code;

import org.springframework.http.HttpStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BaseCode {
	private final HttpStatus httpStatus;
	private final boolean isSuccess;
	private final String code;
	private final String message;
}