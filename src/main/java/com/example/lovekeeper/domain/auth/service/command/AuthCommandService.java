package com.example.lovekeeper.domain.auth.service.command;

import com.example.lovekeeper.domain.auth.dto.request.SignUpRequest;
import com.example.lovekeeper.domain.auth.dto.response.ReissueResponse;
import com.example.lovekeeper.domain.auth.dto.response.SignUpResponse;

public interface AuthCommandService {
	SignUpResponse signUpMember(SignUpRequest signUpRequest);

	ReissueResponse reissueRefreshToken(String oldRefreshToken);
	
}
