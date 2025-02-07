package com.example.lovekeeper.global.security.filter;

import java.io.IOException;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.lovekeeper.domain.auth.dto.response.LoginResponse;
import com.example.lovekeeper.domain.member.model.Member;
import com.example.lovekeeper.domain.member.model.Provider;
import com.example.lovekeeper.global.common.BaseResponse;
import com.example.lovekeeper.global.infrastructure.service.RefreshTokenRedisService;
import com.example.lovekeeper.global.security.jwt.JwtTokenProvider;
import com.example.lovekeeper.global.security.user.CustomUserDetails;
import com.example.lovekeeper.global.security.user.CustomUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

	private final AuthenticationManager authenticationManager;
	private final JwtTokenProvider jwtTokenProvider;
	private final CustomUserDetailsService customUserDetailsService;
	private final ObjectMapper objectMapper;
	private final RefreshTokenRedisService refreshTokenRedisService;

	public LoginFilter(AuthenticationManager authenticationManager,
		JwtTokenProvider jwtTokenProvider,
		CustomUserDetailsService customUserDetailsService,
		ObjectMapper objectMapper,
		RefreshTokenRedisService refreshTokenRedisService
	) {
		this.authenticationManager = authenticationManager;
		this.jwtTokenProvider = jwtTokenProvider;
		this.customUserDetailsService = customUserDetailsService;
		this.objectMapper = objectMapper;
		this.refreshTokenRedisService = refreshTokenRedisService;

		setFilterProcessesUrl("/api/auth/login"); // 인증 처리 URL
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
		throws AuthenticationException {

		try {
			// JSON 형태로 요청 파싱
			Map<String, String> requestMap = objectMapper.readValue(request.getInputStream(), Map.class);

			String providerStr = requestMap.getOrDefault("provider", "local");
			if (providerStr.equalsIgnoreCase("local")) {
				// 로컬 로그인
				String email = requestMap.get("email");
				String password = requestMap.get("password");

				UsernamePasswordAuthenticationToken authRequest =
					new UsernamePasswordAuthenticationToken(email, password);
				return authenticationManager.authenticate(authRequest);
			} else {
				// 소셜 로그인
				String providerId = requestMap.get("providerId");
				Provider providerEnum = mapToProviderEnum(providerStr);

				CustomUserDetails userDetails = customUserDetailsService.loadUserByProvider(providerEnum, providerId);

				UsernamePasswordAuthenticationToken auth =
					new UsernamePasswordAuthenticationToken(
						userDetails,
						null,
						userDetails.getAuthorities()
					);
				return auth;
			}

		} catch (IOException e) {
			log.error("[LoginFilter] 인증 요청 파싱 중 오류 발생", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * String -> Provider enum 매핑
	 */
	private Provider mapToProviderEnum(String providerStr) {
		for (Provider p : Provider.values()) {
			if (p.getKey().equalsIgnoreCase(providerStr)) {
				return p;
			}
		}
		throw new IllegalArgumentException("Invalid provider: " + providerStr);
	}

	/**
	 * 인증 성공 시
	 */
	@Override
	protected void successfulAuthentication(HttpServletRequest request,
		HttpServletResponse response,
		FilterChain chain,
		Authentication authResult) throws IOException {

		CustomUserDetails userDetails = (CustomUserDetails)authResult.getPrincipal();
		Member member = userDetails.getMember();

		long accessValidMs = 30 * 60 * 1000;            // 30분
		long refreshValidMs = 7L * 24 * 60 * 60 * 1000; // 7일

		// 토큰 생성 (예: member.getId() + role)
		String accessToken = jwtTokenProvider.createAccessToken(
			member.getId(),
			member.getRole(),
			accessValidMs
		);
		String refreshToken = jwtTokenProvider.createRefreshToken(
			member.getId(),
			member.getRole(),
			refreshValidMs
		);

		// Redis에 Refresh Token 저장
		refreshTokenRedisService.saveRefreshToken(member.getId(), refreshToken, refreshValidMs);

		// 헤더에 토큰 반환
		response.setHeader("Authorization", "Bearer " + accessToken);
		response.setHeader("Refresh-Token", refreshToken);

		log.info("[LoginFilter] 인증 성공 - provider: {}, email: {}", member.getProvider(), member.getEmail());

		// 응답 DTO
		LoginResponse loginResponse = LoginResponse.from(member);
		BaseResponse<LoginResponse> baseResponse = BaseResponse.onSuccess(loginResponse);

		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().write(objectMapper.writeValueAsString(baseResponse));
	}

	/**
	 * 인증 실패 시
	 */
	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request,
		HttpServletResponse response,
		AuthenticationException failed) throws IOException {
		log.error("[LoginFilter] 로그인 실패 : {}", failed.getMessage());

		// 실패 응답도 BaseResponse 형식으로 전달
		BaseResponse<Object> errorResponse = BaseResponse.onFailure(
			"COMMON401",
			"로그인에 실패하였습니다: " + failed.getMessage(),
			null
		);

		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
	}
}
