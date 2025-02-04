package com.example.lovekeeper.common.response;

import java.time.LocalDateTime;

import com.example.lovekeeper.common.base.exception.code.BaseCodeInterface;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"timestamp", "code", "message", "result"}) // JSON 응답 시 순서를 정의
public class BaseResponse<T> {

	private final LocalDateTime timestamp = LocalDateTime.now();
	private final String code;
	private final String message;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private T result;

	//성공한 경우 응답 생성
	public static <T> BaseResponse<T> onSuccess(T data) {
		return new BaseResponse<>("COMMON200", "요청에 성공하였습니다.", data);
	}

	public static <T> BaseResponse<T> onSuccessCreate(T data) {
		return new BaseResponse<>("COMMON201", "요청에 성공하였습니다.", data);
	}

	public static <T> BaseResponse<T> onSuccessDelete(T data) {
		return new BaseResponse<>("COMMON202", "삭제 요청에 성공하였습니다.", data);
	}

	public static <T> BaseResponse<T> of(BaseCodeInterface code, T data) {
		return new BaseResponse<>(code.getCode().getCode(), code.getCode().getMessage(), data);
	}

	// 실패한 경우 응답 생성
	public static <T> BaseResponse<T> onFailure(String code, String message, T data) {
		return new BaseResponse<>(code, message, data);
	}

}
