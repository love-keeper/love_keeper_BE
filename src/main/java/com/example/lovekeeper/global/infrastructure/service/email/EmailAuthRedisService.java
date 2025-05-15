package com.example.lovekeeper.global.infrastructure.service.email;

import java.time.Duration;
import java.util.Random;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailAuthRedisService {

	private final RedisTemplate<String, String> redisTemplate;

	/**
	 * 6자리 숫자 인증코드 생성
	 */
	public String generateCode() {
		Random random = new Random();
		int code = 100000 + random.nextInt(900000); // 100000 ~ 999999
		return String.valueOf(code);
	}

	/**
	 * 인증 코드 저장 (이메일 주소를 키로 사용)
	 * @param email  대상 이메일
	 * @param code   인증 코드
	 * @param ttlMs  만료 시간 (밀리초)
	 */
	public void saveCode(String email, String code, long ttlMs) {
		String key = "EmailAuth:" + email;
		redisTemplate.opsForValue().set(key, code, Duration.ofMillis(ttlMs));
	}

	/**
	 * 인증 코드 조회
	 * @param email 대상 이메일
	 * @return 저장된 인증 코드 (없으면 null)
	 */
	public String getCode(String email) {
		String key = "EmailAuth:" + email;
		return redisTemplate.opsForValue().get(key);
	}

	/**
	 * 인증 코드 삭제
	 * @param email 대상 이메일
	 */
	public void deleteCode(String email) {
		String key = "EmailAuth:" + email;
		redisTemplate.delete(key);
	}
}
