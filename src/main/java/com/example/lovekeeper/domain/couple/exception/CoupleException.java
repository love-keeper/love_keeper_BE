package com.example.lovekeeper.domain.couple.exception;

import com.example.lovekeeper.global.exception.BaseException;
import com.example.lovekeeper.global.exception.code.BaseCodeInterface;

public class CoupleException extends BaseException {
	public CoupleException(BaseCodeInterface errorCode) {
		super(errorCode);
	}
}
