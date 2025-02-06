package com.example.lovekeeper.global.security.user;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.lovekeeper.domain.member.exception.MemberErrorStatus;
import com.example.lovekeeper.domain.member.exception.MemberException;
import com.example.lovekeeper.domain.member.model.Member;
import com.example.lovekeeper.domain.member.model.Provider;
import com.example.lovekeeper.domain.member.repository.MemberJpaRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final MemberJpaRepository memberJpaRepository;

	/**
	 * 일반적인 시나리오(이메일을 username으로 사용)
	 */
	@Override
	public CustomUserDetails loadUserByUsername(String email) throws MemberException {
		Member member = memberJpaRepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));
		return new CustomUserDetails(member);
	}

	/**
	 * 회원 ID로 회원 조회
	 * (필요 시 MemberJpaRepository에 findById() 등 추가)
	 * (PK를 담았다고 가정 시) Long memberId = jwtTokenProvider.getMemberId(token);
	 */
	public CustomUserDetails loadUserByMemberId(Long memberId) {
		Member member = memberJpaRepository.findById(memberId)
			.orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));
		return new CustomUserDetails(member);
	}

	/**
	 * 소셜 로그인 시나리오: provider와 providerId로 회원 조회
	 * (필요 시 MemberJpaRepository에 findByProviderAndProviderId() 등 추가)
	 */
	public CustomUserDetails loadUserByProvider(Provider provider, String providerId) throws MemberException {
		Member member = memberJpaRepository.findByProviderAndProviderId(provider, providerId)
			.orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));
		return new CustomUserDetails(member);
	}
}
