package com.example.lovekeeper.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "비밀번호 변경 요청")
public class ChangePasswordRequest {

	@Schema(description = "현재 비밀번호", example = "password1234")
	private String currentPassword;

	@Schema(description = "새 비밀번호", example = "password5678")
	private String newPassword;

	@Schema(description = "새 비밀번호 확인", example = "password5678")
	private String newPasswordConfirm;

}
