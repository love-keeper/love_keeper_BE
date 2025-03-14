package com.example.lovekeeper.domain.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
@Schema(description = "닉네임 변경 요청 DTO")
public class ChangeNicknameRequest {

	@Schema(description = "새 닉네임", example = "newlover", required = true)
	@NotBlank(message = "닉네임을 입력해주세요.")
	private String nickname;
}