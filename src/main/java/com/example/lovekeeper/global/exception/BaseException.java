package com.example.lovekeeper.global.exception;

import com.example.lovekeeper.global.exception.code.BaseCode;
import com.example.lovekeeper.global.exception.code.BaseCodeInterface;

import lombok.AllArgsConstructor;

@AllArgsConstructor // 생성자를 만들어주는 어노테이션
public class BaseException extends RuntimeException {

	private final BaseCodeInterface errorCode; //추상화 시킨 인터페이스를 받아서 사용

	//추상화 시킨 ErrorCode의 getrCode()를 사용하여 ErrorCode를 반환
	public BaseCode getErrorCode() {
		return this.errorCode.getCode();
	}
}
