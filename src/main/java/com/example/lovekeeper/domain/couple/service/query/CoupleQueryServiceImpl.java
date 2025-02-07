package com.example.lovekeeper.domain.couple.service.query;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.lovekeeper.domain.couple.exception.CoupleErrorStatus;
import com.example.lovekeeper.domain.couple.exception.CoupleException;
import com.example.lovekeeper.domain.couple.model.Couple;
import com.example.lovekeeper.domain.member.exception.MemberErrorStatus;
import com.example.lovekeeper.domain.member.exception.MemberException;
import com.example.lovekeeper.domain.member.model.Member;
import com.example.lovekeeper.domain.member.repository.MemberJpaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CoupleQueryServiceImpl implements CoupleQueryService {

	private final MemberJpaRepository memberJpaRepository;

	/**
	 * 커플이 사귄 지 며칠이 지났는지 계산
	 * @param memberId 멤버 ID
	 * @return 커플이 사귄 지 경과한 날짜 수
	 */
	public long getDaysSinceStarted(Long memberId) {
		Member currentMember = memberJpaRepository.findById(memberId)
			.orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));

		Couple couple = currentMember.getCouple();

		if (couple == null) {
			throw new CoupleException(CoupleErrorStatus.COUPLE_NOT_FOUND);
		}

		LocalDate startedAt = couple.getStartedAt();
		return ChronoUnit.DAYS.between(startedAt, LocalDate.now()) + 1;
	}
}
