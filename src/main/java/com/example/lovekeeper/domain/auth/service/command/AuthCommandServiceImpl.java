package com.example.lovekeeper.domain.auth.service.command;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.lovekeeper.domain.auth.dto.request.SignUpRequest;
import com.example.lovekeeper.domain.auth.dto.response.SignUpResponse;
import com.example.lovekeeper.domain.member.exception.MemberErrorStatus;
import com.example.lovekeeper.domain.member.exception.MemberException;
import com.example.lovekeeper.domain.member.model.Member;
import com.example.lovekeeper.domain.member.model.Provider;
import com.example.lovekeeper.domain.member.repository.MemberJpaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthCommandServiceImpl implements AuthCommandService {

	private final MemberJpaRepository memberJpaRepository;
	private final PasswordEncoder passwordEncoder;

	/**
	 * 회원가입 로직
	 */
	@Override
	public SignUpResponse signUpMember(SignUpRequest signUpRequest) {
		log.info("회원가입 요청: {}", signUpRequest);

		// 이메일 중복 체크
		if (memberJpaRepository.existsByEmail(signUpRequest.getEmail())) {
			throw new MemberException(MemberErrorStatus.DUPLICATE_EMAIL);
		}
		// TODO: S3에 프로필 이미지 저장하고 URL 저장
		String uploadedImageUrl = null;

		Member member;
		// 로컬 회원가입 사용자라면 비밀번호가 있으므로 인코딩
		if (signUpRequest.getProvider() == Provider.LOCAL) {
			String encodedPassword = passwordEncoder.encode(signUpRequest.getPassword());
			// 새로운 멤버 생성
			member = Member.createLocalMember(signUpRequest.getEmail(), encodedPassword, signUpRequest.getNickname(),
				signUpRequest.getBirthDate(), uploadedImageUrl, signUpRequest.getProvider(),
				signUpRequest.getProviderId());
		} else {
			// 새로운 멤버 생성
			member = Member.createSocialMember(signUpRequest.getEmail(), signUpRequest.getNickname(),
				signUpRequest.getBirthDate(), uploadedImageUrl, signUpRequest.getProvider(),
				signUpRequest.getProviderId());
		}

		// 멤버 저장
		Member savedMember = memberJpaRepository.save(member);

		// 응답 생성
		return SignUpResponse.from(savedMember);

	}
}
