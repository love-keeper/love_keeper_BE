package com.example.lovekeeper.domain.member.service.command;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.lovekeeper.domain.couple.model.CoupleStatus;
import com.example.lovekeeper.domain.member.exception.MemberErrorStatus;
import com.example.lovekeeper.domain.member.exception.MemberException;
import com.example.lovekeeper.domain.member.model.Member;
import com.example.lovekeeper.domain.member.model.MemberStatus;
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
	private final S3Service s3Service;

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
}