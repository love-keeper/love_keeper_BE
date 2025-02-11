package com.example.lovekeeper.domain.couple.service.query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.lovekeeper.domain.couple.dto.response.CountResponse;
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

	/**
	 * 커플이 사귄 지 며칠이 지났는지 계산
	 * @param memberId 멤버 ID
	 * @return 커플이 사귄 지 경과한 날짜 수
	 */
	public long getDaysSinceStarted(Long memberId) {
		Member currentMember = memberRepository.findById(memberId)
			.orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));

		return 1;
	}

	@Override
	public CountResponse getLettersAndPromisesCount(Long memberId) {
		// TODO Auto-generated method stub

		return null;

	}
}
