package com.example.lovekeeper.domain.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
@Schema(description = "비밀번호 변경 요청 DTO")
public class ChangePasswordRequest {

	@Schema(description = "현재 비밀번호", example = "password1234", required = true)
	@NotBlank(message = "현재 비밀번호를 입력해주세요.")
	private String currentPassword;

	@Schema(description = "새 비밀번호", example = "password5678", required = true)
	@NotBlank(message = "새 비밀번호를 입력해주세요.")
	private String newPassword;

	@Schema(description = "새 비밀번호 확인", example = "password5678", required = true)
	@NotBlank(message = "새 비밀번호 확인을 입력해주세요.")
	private String newPasswordConfirm;
}