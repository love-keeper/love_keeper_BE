package com.example.lovekeeper.domain.auth.service.command;

import java.io.IOException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.lovekeeper.domain.auth.dto.request.ChangePasswordAfterResetRequest;
import com.example.lovekeeper.domain.auth.dto.request.SignUpRequest;
import com.example.lovekeeper.domain.auth.dto.response.ReissueResponse;
import com.example.lovekeeper.domain.auth.dto.response.SignUpResponse;
import com.example.lovekeeper.domain.auth.exception.AuthErrorStatus;
import com.example.lovekeeper.domain.auth.exception.AuthException;
import com.example.lovekeeper.domain.couple.repository.CoupleRepository;
import com.example.lovekeeper.domain.member.exception.MemberErrorStatus;
import com.example.lovekeeper.domain.member.exception.MemberException;
import com.example.lovekeeper.domain.member.model.Member;
import com.example.lovekeeper.domain.member.model.Provider;
import com.example.lovekeeper.domain.member.repository.MemberRepository;
import com.example.lovekeeper.global.exception.BaseException;
import com.example.lovekeeper.global.exception.code.GlobalErrorStatus;
import com.example.lovekeeper.global.infrastructure.service.refreshredis.RefreshTokenRedisService;
import com.example.lovekeeper.global.infrastructure.service.s3.S3Service;
import com.example.lovekeeper.global.security.jwt.JwtTokenProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthCommandServiceImpl implements AuthCommandService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;
	private final RefreshTokenRedisService refreshTokenRedisService;
	private final S3Service s3Service;
	private final CoupleRepository coupleRepository;

	/**
	 * 회원가입 로직
	 */
	@Override
	public SignUpResponse signUpMember(SignUpRequest signUpRequest) {
		log.info("회원가입 요청: {}", signUpRequest);

		// 이메일 중복 체크
		if (memberRepository.existsByEmail(signUpRequest.getEmail())) {
			throw new MemberException(MemberErrorStatus.DUPLICATE_EMAIL);
		}

		// 필수 동의 항목 검증
		if (!signUpRequest.isPrivacyPolicyAgreed() || !signUpRequest.isTermsOfServiceAgreed()) {
			throw new AuthException(AuthErrorStatus.CONSENT_REQUIRED);
		}

		String uploadedImageUrl = null;
		try {
			if (signUpRequest.getProfileImage() != null) {

				uploadedImageUrl = s3Service.uploadProfileImage(signUpRequest.getProfileImage(), null);
			}
		} catch (IOException e) {
			log.error("[AuthCommandServiceImpl] S3 이미지 업로드 실패: {}", e.getMessage());
			throw new BaseException(GlobalErrorStatus._S3_UPLOAD_ERROR);
		}

		Member member;
		// 로컬 회원가입 사용자라면 비밀번호가 있으므로 인코딩
		if (signUpRequest.getProvider() == Provider.LOCAL) {
			String encodedPassword = passwordEncoder.encode(signUpRequest.getPassword());
			// 새로운 멤버 생성
			member = Member.createLocalMember(signUpRequest.getEmail(), encodedPassword, signUpRequest.getNickname(),
				signUpRequest.getBirthDate(), uploadedImageUrl, signUpRequest.getProvider(),
				signUpRequest.getProviderId(), signUpRequest.isPrivacyPolicyAgreed(),
				signUpRequest.isMarketingAgreed(), signUpRequest.isTermsOfServiceAgreed());
		} else {
			// 새로운 멤버 생성
			member = Member.createSocialMember(signUpRequest.getEmail(), signUpRequest.getNickname(),
				signUpRequest.getBirthDate(), uploadedImageUrl, signUpRequest.getProvider(),
				signUpRequest.getProviderId(), signUpRequest.isPrivacyPolicyAgreed(),
				signUpRequest.isMarketingAgreed(), signUpRequest.isTermsOfServiceAgreed());
		}

		// 멤버 저장
		Member savedMember = memberRepository.save(member);

		// 응답 생성
		return SignUpResponse.from(savedMember);

	}

	/**
	 * Refresh Token 재발급 로직
	 * @param oldRefreshToken 기존 쿠키에서 가져온 Refresh Token
	 */
	public ReissueResponse reissueRefreshToken(String oldRefreshToken) {

		// 1) 토큰 형식/서명 유효성 검증
		if (!jwtTokenProvider.validateToken(oldRefreshToken)) {
			log.error("[AuthCommandServiceImpl] 유효하지 않은 Refresh Token: {}", oldRefreshToken);
			throw new AuthException(AuthErrorStatus.INVALID_REFRESH_TOKEN);
		}

		// 2) 만료 여부 확인
		if (jwtTokenProvider.isExpired(oldRefreshToken)) {
			log.error("[AuthCommandServiceImpl] 만료된 Refresh Token: {}", oldRefreshToken);
			throw new AuthException(AuthErrorStatus.EXPIRED_TOKEN);
		}

		// 3) 토큰에서 memberId 추출
		Long memberId = jwtTokenProvider.getMemberId(oldRefreshToken);

		// 4) Redis에 저장된 Refresh Token과 일치하는지 검증
		String redisStoredToken = refreshTokenRedisService.getRefreshToken(memberId);
		if (redisStoredToken == null || !redisStoredToken.equals(oldRefreshToken)) {
			log.error("[AuthCommandServiceImpl] Redis에 저장된 Refresh Token 불일치: {}", oldRefreshToken);
			throw new AuthException(AuthErrorStatus.INVALID_REFRESH_TOKEN);
		}

		// 5) 사용자 조회
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new AuthException(AuthErrorStatus.INVALID_TOKEN));

		// 6) 새 Access Token, Refresh Token 생성
		long accessValidMs = 30 * 60 * 1000;            // 30분
		long refreshValidMs = 7L * 24 * 60 * 60 * 1000; // 7일
		String newAccessToken = jwtTokenProvider.createAccessToken(member.getId(), member.getRole(), accessValidMs);
		String newRefreshToken = jwtTokenProvider.createRefreshToken(member.getId(), member.getRole(), refreshValidMs);

		// 7) Redis 토큰 업데이트
		refreshTokenRedisService.deleteRefreshToken(member.getId());
		refreshTokenRedisService.saveRefreshToken(member.getId(), newRefreshToken, refreshValidMs);

		// 8) 새 토큰들 반환
		return ReissueResponse.of(newAccessToken, newRefreshToken);
	}

	@Override
	public void resetPassword(ChangePasswordAfterResetRequest request) {
		// 1) 사용자 조회
		Member currentMember = memberRepository.findByEmail(request.getEmail())
			.orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));

		// 2) 새 비밀번호와 새 비밀번호 확인 일치 여부 확인
		if (!request.getPassword().equals(request.getPasswordConfirm())) {
			throw new MemberException(MemberErrorStatus.PASSWORD_MISMATCH);
		}

		// 3) 비밀번호 변경
		currentMember.changePassword(passwordEncoder.encode(request.getPassword()));
	}

}
