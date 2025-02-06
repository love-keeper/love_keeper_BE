package com.example.lovekeeper.domain.member.exception;

import com.example.lovekeeper.global.exception.BaseException;
import com.example.lovekeeper.global.exception.code.BaseCodeInterface;

public class MemberException extends BaseException {

	public MemberException(BaseCodeInterface errorCode) {
		super(errorCode);
	}
}