package com.example.lovekeeper.domain.draft.exception;

import com.example.lovekeeper.global.exception.BaseException;
import com.example.lovekeeper.global.exception.code.BaseCodeInterface;

public class DraftException extends BaseException {
	public DraftException(BaseCodeInterface errorCode) {
		super(errorCode);
	}
}
