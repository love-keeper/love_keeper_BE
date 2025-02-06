package com.example.lovekeeper.domain.auth.exception;

import com.example.lovekeeper.global.exception.BaseException;
import com.example.lovekeeper.global.exception.code.BaseCodeInterface;

public class AuthException extends BaseException {
	public AuthException(BaseCodeInterface errorCode) {
		super(errorCode);
	}
}
