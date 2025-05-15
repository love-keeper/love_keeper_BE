package com.example.lovekeeper.domain.auth.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.lovekeeper.domain.auth.dto.request.SendEmailCodeRequest;
import com.example.lovekeeper.domain.auth.dto.request.VerifyEmailCodeRequest;
import com.example.lovekeeper.domain.auth.dto.response.SendCodeResponse;
import com.example.lovekeeper.domain.auth.service.command.EmailAuthCommandService;
import com.example.lovekeeper.global.common.BaseResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class EmailAuthController {

	private final EmailAuthCommandService emailCommandAuthService;

	/**
	 * 이메일 인증 코드 발송
	 */
	@PostMapping("/send-code")
	public BaseResponse<SendCodeResponse> sendEmailCode(
		@RequestBody @Valid SendEmailCodeRequest request) {

		return BaseResponse.onSuccess(emailCommandAuthService.sendVerificationCode(request.getEmail()));
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
