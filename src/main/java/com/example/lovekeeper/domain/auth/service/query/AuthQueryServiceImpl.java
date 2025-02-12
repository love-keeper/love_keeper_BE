package com.example.lovekeeper.domain.auth.service.query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.lovekeeper.domain.auth.exception.AuthErrorStatus;
import com.example.lovekeeper.domain.auth.exception.AuthException;
import com.example.lovekeeper.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthQueryServiceImpl implements AuthQueryService {

	private final MemberRepository memberRepository;

	@Override
	public void checkEmailDuplication(String email) {
		log.info("checkEmailDuplication: {}", email);
		memberRepository.findByEmail(email).ifPresent(member -> {
			throw new AuthException(AuthErrorStatus.DUPLICATE_EMAIL);
		});
	}
}
