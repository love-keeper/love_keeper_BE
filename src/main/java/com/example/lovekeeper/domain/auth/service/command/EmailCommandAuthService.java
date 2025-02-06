package com.example.lovekeeper.domain.auth.service.command;

public interface EmailCommandAuthService {

	void sendVerificationCode(String email);

	void verifyCode(String email, String code);

}
