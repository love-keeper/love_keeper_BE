package com.example.lovekeeper.domain.auth.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.lovekeeper.domain.auth.dto.request.SendEmailCodeRequest;
import com.example.lovekeeper.domain.auth.dto.request.VerifyEmailCodeRequest;
import com.example.lovekeeper.domain.auth.service.command.EmailCommandAuthService;
import com.example.lovekeeper.global.common.BaseResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class EmailAuthController {

	private final EmailCommandAuthService emailCommandAuthService;

	/**
	 * 이메일 인증 코드 발송
	 */
	@PostMapping("/send-code")
	public BaseResponse<String> sendEmailCode(@RequestBody SendEmailCodeRequest request) {
		emailCommandAuthService.sendVerificationCode(request.getEmail());
		return BaseResponse.onSuccess("인증 코드가 이메일로 발송되었습니다.");
	}

	/**
	 * 이메일 인증 코드 검증
	 */
	@PostMapping("/verify-code")
	public BaseResponse<String> verifyEmailCode(@RequestBody VerifyEmailCodeRequest request) {
		emailCommandAuthService.verifyCode(request.getEmail(), request.getCode());
		return BaseResponse.onSuccess("인증 성공");
	}
}
