package com.example.lovekeeper.domain.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * POST /api/auth/login
 * - 일반 로그인(email, password)
 * - 소셜 로그인(provider, providerId)
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
	private String email;
	private String password;

	private String provider;   // 예) "NAVER", "KAKAO" 등
	private String providerId; // 소셜 식별자
}
