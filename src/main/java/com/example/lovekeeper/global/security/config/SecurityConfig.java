package com.example.lovekeeper.global.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.lovekeeper.global.infrastructure.service.RefreshTokenRedisService;
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
	private final ObjectMapper objectMapper; // 전역 Bean (JacksonConfig 등에서 주입)
	private final RefreshTokenRedisService refreshTokenRedisService;

	/**
	 * AuthenticationManager 빈 등록
	 */
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	/**
	 * 패스워드 인코더
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * 보안 설정
	 */
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		// AuthenticationManager 주입
		AuthenticationManager authenticationManager = authenticationManager(
			http.getSharedObject(AuthenticationConfiguration.class));

		// LoginFilter 생성 (provider별 local or social 로그인 처리)
		LoginFilter loginFilter = new LoginFilter(
			authenticationManager,
			jwtTokenProvider,
			customUserDetailsService,
			objectMapper,
			refreshTokenRedisService
		);

		http
			.csrf(csrf -> csrf.disable())
			.sessionManagement(session -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.exceptionHandling(exception -> exception
				.authenticationEntryPoint(jwtAuthenticationEntryPoint)
				.accessDeniedHandler(jwtAccessDeniedHandler))
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/api/auth/**").permitAll()       // 로그인 요청 허용
				.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
				.requestMatchers("/health").permitAll()
				.anyRequest().authenticated()
			)
			.cors(Customizer.withDefaults()); // 별도 CORS 설정이 있다면 적용 (corsConfigurationSource 등)

		// 필터 등록 순서:
		// 1) LoginFilter ( /api/auth/login ) 처리
		// 2) JwtAuthenticationFilter ( JWT 검증 )
		http.addFilterBefore(loginFilter, UsernamePasswordAuthenticationFilter.class);
		http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	// 필요 시 CORS 설정
    /*
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        // ...
        return source;
    }
    */
}
