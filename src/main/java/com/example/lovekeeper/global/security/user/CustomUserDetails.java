package com.example.lovekeeper.global.security.user;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.lovekeeper.domain.member.model.Member;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Member 엔티티를 Spring Security의 UserDetails로 변환한 클래스
 * 인증/인가 과정에서 활용됨
 */
@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

	private final Member member; // Member 엔티티 자체를 보관

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// 본 예시는 Role 1개만 있다고 가정하고, Spring Security 용으로 GrantedAuthority 변환
		return Collections.singletonList((GrantedAuthority)() -> member.getRole().name());
	}

	public Member getMember() {
		return member;
	}

	@Override
	public String getPassword() {
		return member.getPassword(); // DB에 저장된 해시 PW
	}

	@Override
	public String getUsername() {
		return member.getEmail(); // username으로 email 사용
	}

	@Override
	public boolean isAccountNonExpired() {
		return true; // 예시로 바로 true 처리
	}

	@Override
	public boolean isAccountNonLocked() {
		return true; // 예시로 바로 true 처리
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true; // 예시로 바로 true 처리
	}

	@Override
	public boolean isEnabled() {
		// 예시로 member 상태가 ACTIVE인 경우 true, 아닌 경우 false 처리 가능
		return member.getMemberStatus().isActive();
	}
}
