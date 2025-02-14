package com.example.lovekeeper.domain.fcm.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
@Schema(description = "FCM 토큰 요청")
public class FCMTokenRequest {

	@NotBlank(message = "FCM 토큰은 필수입니다.")
	private String token;
	
}