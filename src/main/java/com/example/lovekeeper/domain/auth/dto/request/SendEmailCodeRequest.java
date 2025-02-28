package com.example.lovekeeper.domain.auth.dto.request;

import com.example.lovekeeper.domain.member.exception.annotation.UniqueEmail;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Schema(description = "이메일 코드 전송 요청")
public class SendEmailCodeRequest {

	@Schema(description = "이메일", example = "qkrehdrb0813@gmail.com")
	@UniqueEmail
	private String email;

}
