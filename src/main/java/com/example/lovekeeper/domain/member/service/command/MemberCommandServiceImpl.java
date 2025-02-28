package com.example.lovekeeper.domain.member.service.command;

import java.io.IOException;
import java.time.LocalDate;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.lovekeeper.domain.auth.service.command.EmailAuthCommandService;
import com.example.lovekeeper.domain.couple.exception.CoupleErrorStatus;
import com.example.lovekeeper.domain.couple.exception.CoupleException;
import com.example.lovekeeper.domain.couple.model.Couple;
import com.example.lovekeeper.domain.couple.model.CoupleStatus;
import com.example.lovekeeper.domain.couple.repository.CoupleRepository;
import com.example.lovekeeper.domain.member.dto.request.ChangePasswordRequest;
import com.example.lovekeeper.domain.member.dto.response.ChangeBirthdayResponse;
import com.example.lovekeeper.domain.member.dto.response.ChangeNicknameResponse;
import com.example.lovekeeper.domain.member.dto.response.MyInfoResponse;
import com.example.lovekeeper.domain.member.exception.MemberErrorStatus;
import com.example.lovekeeper.domain.member.exception.MemberException;
import com.example.lovekeeper.domain.member.model.Member;
import com.example.lovekeeper.domain.member.model.MemberStatus;
import com.example.lovekeeper.domain.member.model.Provider;
import com.example.lovekeeper.domain.member.repository.MemberRepository;
import com.example.lovekeeper.global.exception.BaseException;
import com.example.lovekeeper.global.exception.code.GlobalErrorStatus;
import com.example.lovekeeper.global.infrastructure.service.s3.S3Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberCommandServiceImpl implements MemberCommandService {

	private final MemberRepository memberRepository;
	private final CoupleRepository coupleRepository;
	private final S3Service s3Service;
	private final PasswordEncoder passwordEncoder;
	private final EmailAuthCommandService emailAuthCommandService;

	@Override
	public MyInfoResponse getMyInfo(Long memberId) {
		// 1) 사용자 조회
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));

		Couple couple = coupleRepository.findByMemberId(member.getId())
			.orElseThrow(() -> new CoupleException(CoupleErrorStatus.COUPLE_NOT_FOUND));

		return MyInfoResponse.of(
			member.getId(),
			member.getNickname(),
			member.getBirthDay(),
			couple.getStartedAt(),
			member.getEmail(),
			member.getProfileImageUrl()
		);
	}

	@Override
	public void changePassword(Long memberId, ChangePasswordRequest request) {
		// 1) 사용자 조회
		Member currentMember = memberRepository.findById(memberId)
			.orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));

		// 2) 사용자가 SNS 로그인 회원인지 확인
		if (currentMember.isSocialMember()) {
			throw new MemberException(MemberErrorStatus.SOCIAL_MEMBER);
		}

		// 3) 현재 비밀번호 일치 여부 확인
		if (!passwordEncoder.matches(request.getCurrentPassword(), currentMember.getPassword())) {
			throw new MemberException(MemberErrorStatus.INVALID_CURRENT_PASSWORD);
		}

		// 4) 새 비밀번호와 현재 비밀번호 일치 여부 확인
		if (passwordEncoder.matches(request.getNewPassword(), currentMember.getPassword())) {
			throw new MemberException(MemberErrorStatus.SAME_AS_CURRENT_PASSWORD);
		}

		// 5) 새 비밀번호와 새 비밀번호 확인 일치 여부 확인
		if (!request.getNewPassword().equals(request.getNewPasswordConfirm())) {
			throw new MemberException(MemberErrorStatus.PASSWORD_MISMATCH);
		}

		// 6) 비밀번호 변경
		currentMember.changePassword(passwordEncoder.encode(request.getNewPassword()));

	}

	@Override
	public void withdrawMember(Long memberId) {
		// 1. 회원 조회
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));

		// 2. 이미 탈퇴한 회원인지 확인
		if (member.isDeleted() && member.getStatus() == MemberStatus.DELETED) {
			throw new MemberException(MemberErrorStatus.MEMBER_ALREADY_DELETED);
		}

		// 3. 현재 활성화된 커플 관계가 있는지 확인하고 처리
		member.getActiveCouple().ifPresent(couple -> {
			// 3-1. 커플 연결 끊기
			couple.disconnect();

			// 3-2. 파트너의 상태도 업데이트
			Member partner = couple.getPartner(member);
			partner.updateCoupleStatus(CoupleStatus.DISCONNECTED);

			log.info("커플 연결 해제 - memberId: {}, partnerId: {}", memberId, partner.getId());
		});

		// 4. Soft Delete 처리
		member.delete();  // BaseEntity의 delete() 메서드 호출하여 deletedAt 설정

		// 5. 회원 상태 변경
		member.updateStatus(MemberStatus.DELETED);
		member.updateCoupleStatus(CoupleStatus.DISCONNECTED);

		log.info("회원 탈퇴 처리 완료 - memberId: {}", memberId);
	}

	@Override
	public void updateProfileImage(Long memberId, MultipartFile profileImage) {
		try {
			// 1. 회원 조회
			Member currentMember = memberRepository.findById(memberId)
				.orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));

			// 2. S3에 이미지 업로드
			String newProfileImageUrl = s3Service.uploadProfileImage(profileImage, currentMember.getProfileImageUrl());

			// 3. 회원 프로필 이미지 URL 업데이트
			currentMember.updateProfileImageUrl(newProfileImageUrl);

			log.info("프로필 이미지 업로드 완료 - memberId: {}", memberId);

		} catch (IOException e) {
			throw new BaseException(GlobalErrorStatus._S3_UPLOAD_ERROR);
		}
	}

	@Override
	public ChangeNicknameResponse changeNickname(Long memberId, String nickname) {
		// 1) 사용자 조회
		Member currentMember = memberRepository.findById(memberId)
			.orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));

		// 2) 내 커플과 닉네임이 똑같은지 검증

		// 우선 파트너 정보를 가져오기 위해 커플 정보를 가져옴
		Couple currentCouple = coupleRepository.findByMemberId(memberId)
			.orElseThrow(() -> new CoupleException(CoupleErrorStatus.COUPLE_NOT_FOUND));

		// 파트너 정보를 가져옴
		Member partner;
		if (currentCouple.getMember1().equals(currentCouple)) {
			partner = currentCouple.getMember2();
		} else {
			partner = currentCouple.getMember1();
		}

		// 같으면 안됨
		if (currentMember.getNickname().equals(partner.getNickname())) {
			throw new CoupleException(CoupleErrorStatus.SAME_NICKNAME);
		}

		// 3) 닉네임 변경
		currentMember.changeNickname(nickname);

		return ChangeNicknameResponse.of(nickname);
	}

	@Override
	public ChangeBirthdayResponse changeBirthday(Long memberId, LocalDate birthday) {
		// 1) 사용자 조회
		Member currentMember = memberRepository.findById(memberId)
			.orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));

		currentMember.changeBirthday(birthday);

		return ChangeBirthdayResponse.of(birthday);
	}

	// 기존 changeEmail 메서드를 대체하는 새로운 메서드
	public void changeEmailWithVerification(Long memberId, String email, String code) {
		// 1) 사용자 조회
		Member currentMember = memberRepository.findById(memberId)
			.orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));

		// 2) 소셜 로그인 회원인지 체크
		// 소셜 로그인 회원은 이메일 변경 불가
		if (currentMember.getProvider() != Provider.LOCAL) {
			throw new MemberException(MemberErrorStatus.SOCIAL_MEMBER_EMAIL_CANNOT_CHANGE);
		}

		// 3) 이메일 중복 체크
		if (memberRepository.existsByEmail(email)) {
			throw new MemberException(MemberErrorStatus.DUPLICATE_EMAIL);
		}

		// 4) 인증 코드 검증
		emailAuthCommandService.verifyCode(email, code);

		// 5) 이메일 변경
		currentMember.changeEmail(email);

		log.info("이메일 변경 완료 - memberId: {}, newEmail: {}", memberId, email);
	}
}