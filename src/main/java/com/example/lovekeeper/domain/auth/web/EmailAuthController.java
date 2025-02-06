package com.example.lovekeeper.domain.auth.web;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
	public BaseResponse<String> sendEmailCode(@RequestParam("email") String email) {
		emailCommandAuthService.sendVerificationCode(email);
		// 성공적으로 코드 발송을 시도한 경우
		return BaseResponse.onSuccess("인증 코드가 이메일로 발송되었습니다.");
	}

	/**
	 * 이메일 인증 코드 검증
	 */
	@PostMapping("/verify-code")
	public BaseResponse<String> verifyEmailCode(@RequestParam("email") String email,
		@RequestParam("code") String code) {
		// 내부에서 인증 실패 시 BaseException 발생 -> 전역 예외 처리로 에러 응답
		emailCommandAuthService.verifyCode(email, code);
		return BaseResponse.onSuccess("인증 성공");
	}
}
