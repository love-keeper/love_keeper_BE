package com.example.lovekeeper.domain.couple.service.query;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.lovekeeper.domain.couple.dto.response.CountResponse;
import com.example.lovekeeper.domain.couple.dto.response.CoupleInfoResponse;
import com.example.lovekeeper.domain.couple.exception.CoupleErrorStatus;
import com.example.lovekeeper.domain.couple.exception.CoupleException;
import com.example.lovekeeper.domain.couple.model.Couple;
import com.example.lovekeeper.domain.couple.repository.CoupleRepository;
import com.example.lovekeeper.domain.member.exception.MemberErrorStatus;
import com.example.lovekeeper.domain.member.exception.MemberException;
import com.example.lovekeeper.domain.member.model.Member;
import com.example.lovekeeper.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CoupleQueryServiceImpl implements CoupleQueryService {

	private final MemberRepository memberRepository;
	private final CoupleRepository coupleRepository;

	/**
	 * 커플이 사귄 지 며칠이 지났는지 계산
	 * @param memberId 멤버 ID
	 * @return 커플이 사귄 지 경과한 날짜 수
	 */
	public long getDaysSinceStarted(Long memberId) {
		Member currentMember = memberRepository.findById(memberId)
			.orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));

		// 커플 조회
		Couple couple = coupleRepository.findByMemberId(memberId)
			.orElseThrow(() -> new CoupleException(CoupleErrorStatus.COUPLE_NOT_FOUND));

		// 커플 시작일
		return getDays(couple.getStartedAt());

	}

	@Override
	public CountResponse getLettersAndPromisesCount(Long memberId) {
		// TODO Auto-generated method stub

		return null;

	}

	/**
	 * 커플 시작일 조회
	 */
	@Override
	public LocalDate getCoupleStartDate(Long memberId) {

		return coupleRepository.findByMemberId(memberId)
			.orElseThrow(() -> new CoupleException(CoupleErrorStatus.COUPLE_NOT_FOUND)).getStartedAt();
	}

	@Override
	public CoupleInfoResponse getCoupleInfo(Long memberId) {
		// 현재 사용자 조회
		Member currentMember = memberRepository.findById(memberId)
			.orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));

		// 커플 조회
		Couple couple = coupleRepository.findByMemberId(memberId)
			.orElseThrow(() -> new CoupleException(CoupleErrorStatus.COUPLE_NOT_FOUND));

		Member partner = couple.getPartner(currentMember);

		return CoupleInfoResponse.of(
			couple.getId(),
			partner.getNickname(),
			currentMember.getProfileImageUrl(),
			partner.getProfileImageUrl(),
			couple.getStartedAt(),
			getDays(couple.getStartedAt()),
			couple.getStatus(),
			partner.getEmail(),
			currentMember.getEmail(),
			couple.getEndedAt()
		);
	}

	private long getDays(LocalDate startDate) {
		return ChronoUnit.DAYS.between(startDate, LocalDate.now()) + 1;
	}

}
