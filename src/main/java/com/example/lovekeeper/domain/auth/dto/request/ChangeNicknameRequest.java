package com.example.lovekeeper.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ChangeNicknameRequest {

	@NotBlank
	private String nickname;

}
