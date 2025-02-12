package com.example.lovekeeper.global.security.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.lovekeeper.domain.auth.exception.AuthErrorStatus;
import com.example.lovekeeper.domain.auth.exception.AuthException;
import com.example.lovekeeper.domain.member.model.Role;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtTokenProvider {

	private final SecretKey secretKey;

	// JwtTokenProvider
	public JwtTokenProvider(@Value("${jwt.secret}") String secret) {
		// HS512를 쓰려면 64바이트 이상이어야 함
		this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * member의 PK와 role을 담아 토큰 생성
	 */
	public String createAccessToken(Long memberId, Role role, long validMs) {
		return Jwts.builder()
			.claim("memberId", memberId)
			.claim("role", role.name())
			.setIssuedAt(new Date())
			.setExpiration(new Date(System.currentTimeMillis() + validMs))
			.signWith(secretKey)
			.compact();
	}

	public String createRefreshToken(Long memberId, Role role, long validMs) {
		return Jwts.builder()
			.claim("memberId", memberId)
			.claim("role", role.name())
			.setIssuedAt(new Date())
			.setExpiration(new Date(System.currentTimeMillis() + validMs))
			.signWith(secretKey)
			.compact();
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token);
			return true;
		} catch (Exception e) {
			log.error("[JwtTokenProvider] validateToken() - 유효하지 않은 토큰: {}", e.getMessage());
			return false;
		}
	}

	public boolean isExpired(String token) {
		try {
			Date expiration = Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.getExpiration();
			return expiration.before(new Date());
		} catch (Exception e) {
			// 예외 발생 시 만료로 간주
			return true;
		}
	}

	/**
	 * 토큰에서 memberId 추출
	 */
	public Long getMemberId(String token) {
		try {
			return Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.get("memberId", Long.class);
		} catch (Exception e) {
			throw new AuthException(AuthErrorStatus.INVALID_TOKEN);
		}
	}

	/**
	 * 토큰에서 role 추출
	 */
	public String getRole(String token) {
		try {
			return Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.get("role", String.class);
		} catch (Exception e) {
			throw new AuthException(AuthErrorStatus.INVALID_TOKEN);
		}
	}
}
