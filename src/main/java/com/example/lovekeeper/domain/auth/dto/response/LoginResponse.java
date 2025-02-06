package com.example.lovekeeper.domain.auth.dto.response;

import com.example.lovekeeper.domain.member.model.Member;
import com.example.lovekeeper.domain.member.model.Provider;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 로그인 성공시 내려줄 응답 예시
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
	private Long memberId;
	private String email;
	private boolean isSocial;
	private String role;

	public static LoginResponse from(Member member) {
		return LoginResponse.builder()
			.memberId(member.getId())
			.email(member.getEmail())
			.isSocial(!Provider.LOCAL.equals(member.getProvider()))
			.role(member.getRole().name())
			.build();
	}
}
