package com.example.lovekeeper.domain.auth.service.command;

import com.example.lovekeeper.domain.auth.dto.response.SendCodeResponse;

public interface EmailAuthCommandService {

	SendCodeResponse sendVerificationCode(String email);

	void verifyCode(String email, String code);

	void sendPasswordChangeLink(String email);

}
