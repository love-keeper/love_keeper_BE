package com.example.lovekeeper.global.infrastructure.service.refreshredis;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenRedisService {

	private final RedisTemplate<String, String> redisTemplate;

	/**
	 * Refresh Token 저장
	 *
	 * @param memberId                사용자 PK
	 * @param refreshToken            발급된 Refresh Token
	 * @param refreshTokenValidityMs  토큰 만료 시간(밀리초)
	 */
	public void saveRefreshToken(Long memberId, String refreshToken, long refreshTokenValidityMs) {
		String key = "RT:" + memberId;
		Duration expiration = Duration.ofMillis(refreshTokenValidityMs);

		// Redis에 저장 + TTL 설정
		redisTemplate.opsForValue().set(key, refreshToken, expiration);
	}

	/**
	 * Refresh Token 조회
	 */
	public String getRefreshToken(Long memberId) {
		String key = "RT:" + memberId;
		return redisTemplate.opsForValue().get(key);
	}

	/**
	 * Refresh Token 삭제 (로그아웃 등)
	 */
	public void deleteRefreshToken(Long memberId) {
		String key = "RT:" + memberId;
		redisTemplate.delete(key);
	}
}

