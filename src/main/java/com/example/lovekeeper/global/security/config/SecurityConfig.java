package com.example.lovekeeper.global.security.config;

import java.util.List;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.lovekeeper.domain.member.service.command.MemberCommandService;
import com.example.lovekeeper.global.infrastructure.service.refreshredis.RefreshTokenRedisService;
import com.example.lovekeeper.global.security.filter.JwtAuthenticationFilter;
import com.example.lovekeeper.global.security.filter.LoginFilter;
import com.example.lovekeeper.global.security.handler.JwtAccessDeniedHandler;
import com.example.lovekeeper.global.security.handler.JwtAuthenticationEntryPoint;
import com.example.lovekeeper.global.security.jwt.JwtTokenProvider;
import com.example.lovekeeper.global.security.user.CustomUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtTokenProvider jwtTokenProvider;
	private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final CustomUserDetailsService customUserDetailsService;
	private final ObjectMapper objectMapper;
	private final RefreshTokenRedisService refreshTokenRedisService;
	private final MemberCommandService memberCommandService;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		// AuthenticationManager 주입
		AuthenticationManager authenticationManager = authenticationManager(
			http.getSharedObject(AuthenticationConfiguration.class));

		// LoginFilter 생성
		LoginFilter loginFilter = new LoginFilter(
			authenticationManager,
			jwtTokenProvider,
			customUserDetailsService,
			objectMapper,
			refreshTokenRedisService,
			memberCommandService
		);

		http
			.csrf(csrf -> csrf.disable())
			.sessionManagement(session -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.exceptionHandling(exception -> exception
				.authenticationEntryPoint(jwtAuthenticationEntryPoint)
				.accessDeniedHandler(jwtAccessDeniedHandler))
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()  // OPTIONS 요청 허용
				.requestMatchers("/error").permitAll()                   // 에러 페이지 허용
				.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()  // 정적 리소스 허용
				.requestMatchers("/api/auth/**").permitAll()       // 로그인 요청 허용
				.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/swagger-ui.html",
					"/webjars/**").permitAll()
				.requestMatchers("/health").permitAll()
				.requestMatchers("/actuator/**").permitAll()
				.anyRequest().authenticated()
			)
			.cors(Customizer.withDefaults());

		// 필터 순서:
		// 1) ExceptionTranslationFilter를 먼저 등록하여 404 등의 에러를 처리
		http.addFilterBefore(new ExceptionTranslationFilter(jwtAuthenticationEntryPoint),
			FilterSecurityInterceptor.class);

		// 2) 그 다음 JWT 인증 필터들 등록
		http.addFilterBefore(loginFilter, UsernamePasswordAuthenticationFilter.class);
		http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	/**
	 * CORS 설정
	 */
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of("*"));
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(List.of("*"));
		configuration.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}