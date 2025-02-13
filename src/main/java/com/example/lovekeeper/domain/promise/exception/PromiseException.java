package com.example.lovekeeper.domain.promise.exception;

import com.example.lovekeeper.global.exception.BaseException;
import com.example.lovekeeper.global.exception.code.BaseCodeInterface;

public class PromiseException extends BaseException {
	public PromiseException(BaseCodeInterface errorCode) {
		super(errorCode);
	}
}
