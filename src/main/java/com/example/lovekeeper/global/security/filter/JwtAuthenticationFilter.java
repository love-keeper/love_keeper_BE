package com.example.lovekeeper.global.security.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.lovekeeper.global.common.BaseResponse;
import com.example.lovekeeper.global.security.jwt.JwtTokenProvider;
import com.example.lovekeeper.global.security.user.CustomUserDetails;
import com.example.lovekeeper.global.security.user.CustomUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;
	private final CustomUserDetailsService customUserDetailsService;
	private final ObjectMapper objectMapper; // (Bean 등록 또는 생성자 주입)

	@Override
	protected void doFilterInternal(HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		try {
			String token = resolveToken(request);

			// 토큰이 존재하고 유효하다면
			if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
				// 1) 토큰에서 memberId(PK) 추출
				Long memberId = jwtTokenProvider.getMemberId(token);

				// 2) DB에서 사용자 정보를 가져와 SecurityContext에 세팅
				CustomUserDetails userDetails = customUserDetailsService.loadUserByMemberId(memberId);
				UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
					userDetails,
					null,
					userDetails.getAuthorities()
				);

				SecurityContextHolder.getContext().setAuthentication(auth);

				log.debug("[JwtAuthenticationFilter] 인증 성공 - memberId: {}", memberId);
			}

			// 다음 필터로 진행
			filterChain.doFilter(request, response);

		} catch (ExpiredJwtException e) {
			log.error("[JwtAuthenticationFilter] 만료된 토큰 : {}", e.getMessage());
			makeErrorResponse(response, "COMMON401", "만료된 토큰입니다.");

		} catch (Exception e) {
			log.error("[JwtAuthenticationFilter] 토큰 검증 중 예외 발생 : {}", e.getMessage());
			makeErrorResponse(response, "COMMON401", "유효하지 않은 토큰입니다.");
		}
	}

	/**
	 * Authorization 헤더에서 Bearer 토큰 추출
	 */
	private String resolveToken(HttpServletRequest request) {
		String bearer = request.getHeader("Authorization");
		if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
			return bearer.substring(7);
		}
		return null;
	}

	/**
	 * 예외 상황 시 BaseResponse로 401 응답
	 */
	private void makeErrorResponse(HttpServletResponse response, String code, String message) throws IOException {
		BaseResponse<Object> errorResponse = BaseResponse.onFailure(code, message, null);
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
	}
}
