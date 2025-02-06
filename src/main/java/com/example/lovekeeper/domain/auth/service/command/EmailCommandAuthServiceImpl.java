package com.example.lovekeeper.domain.auth.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.example.lovekeeper.domain.auth.exception.AuthErrorStatus;
import com.example.lovekeeper.domain.auth.service.command.EmailCommandAuthService;
import com.example.lovekeeper.global.exception.BaseException;
import com.example.lovekeeper.global.infrastructure.service.EmailAuthRedisService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailCommandAuthServiceImpl implements EmailCommandAuthService {

	private static final long CODE_EXPIRE_TIME_MS = 300000;
	private final JavaMailSender mailSender;
	private final TemplateEngine templateEngine;
	private final EmailAuthRedisService emailAuthRedisService;

	/**
	 * 이메일로 인증 코드 발송
	 * @param email 수신자 이메일
	 */
	@Override
	public void sendVerificationCode(String email) {
		// 1. 6자리 숫자 인증 코드 생성
		String code = emailAuthRedisService.generateCode();

		// 2. Redis에 인증 코드 저장
		emailAuthRedisService.saveCode(email, code, CODE_EXPIRE_TIME_MS);

		// 3. Thymeleaf Context 생성
		Context context = new Context();
		context.setVariable("code", code);

		// 4. HTML 내용 생성 (verification.html 템플릿 사용)
		String htmlContent = templateEngine.process("verification", context);

		// 5. 메일 발송 시도
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

			helper.setTo(email);
			helper.setSubject("love keeper 이메일 인증 코드");
			helper.setText(htmlContent, true); // HTML 사용

			mailSender.send(message);
		} catch (MessagingException e) {
			// 메일 발송 실패 시 BaseException을 발생
			throw new BaseException(AuthErrorStatus.EMAIL_SEND_FAIL);
		}
	}

	/**
	 * 인증 코드 검증
	 * @param email 사용자 이메일
	 * @param code  사용자가 입력한 인증 코드
	 */
	@Override
	public void verifyCode(String email, String code) {
		String savedCode = emailAuthRedisService.getCode(email);

		// 인증 코드가 없거나 불일치하면 예외 발생
		if (savedCode == null || !savedCode.equals(code)) {
			throw new BaseException(AuthErrorStatus.EMAIL_VERIFICATION_FAILED);
		}

		// 인증 성공 시 재사용 방지 위해 Redis에서 삭제
		emailAuthRedisService.deleteCode(email);
	}
}
