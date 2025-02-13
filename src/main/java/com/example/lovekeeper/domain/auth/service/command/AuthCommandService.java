package com.example.lovekeeper.domain.auth.service.command;

import java.time.LocalDate;

import com.example.lovekeeper.domain.auth.dto.request.ChangePasswordRequest;
import com.example.lovekeeper.domain.auth.dto.request.SignUpRequest;
import com.example.lovekeeper.domain.auth.dto.response.ChangeBirthdayResponse;
import com.example.lovekeeper.domain.auth.dto.response.ChangeNicknameResponse;
import com.example.lovekeeper.domain.auth.dto.response.ReissueResponse;
import com.example.lovekeeper.domain.auth.dto.response.SignUpResponse;

public interface AuthCommandService {
	SignUpResponse signUpMember(SignUpRequest signUpRequest);

	ReissueResponse reissueRefreshToken(String oldRefreshToken);

	ChangeNicknameResponse changeNickname(Long memberId, String nickname);

	ChangeBirthdayResponse changeBirthday(Long memberId, LocalDate birthday);

	void changePassword(Long memberId, ChangePasswordRequest request);

}
