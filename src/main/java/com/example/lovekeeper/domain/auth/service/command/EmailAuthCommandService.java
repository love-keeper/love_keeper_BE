package com.example.lovekeeper.domain.auth.service.command;

public interface EmailAuthCommandService {

	void sendVerificationCode(String email);

	void verifyCode(String email, String code);

	void sendPasswordChangeLink(String email);

}
