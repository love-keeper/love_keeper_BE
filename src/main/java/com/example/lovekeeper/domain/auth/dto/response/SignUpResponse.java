package com.example.lovekeeper.domain.auth.dto.response;

import com.example.lovekeeper.domain.member.model.Member;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignUpResponse {
	private Long memberId;

	public static SignUpResponse from(Member member) {
		return SignUpResponse.builder()
			.memberId(member.getId())
			.build();
	}
}