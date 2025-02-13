package com.example.lovekeeper.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ChangeNicknameRequest {

	@NotBlank(message = "닉네임을 입력해주세요.")
	private String nickname;

}
