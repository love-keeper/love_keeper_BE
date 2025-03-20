package com.example.lovekeeper.domain.draft.service.query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.lovekeeper.domain.draft.dto.response.DraftResponse;
import com.example.lovekeeper.domain.draft.exception.DraftErrorStatus;
import com.example.lovekeeper.domain.draft.exception.DraftException;
import com.example.lovekeeper.domain.draft.model.Draft;
import com.example.lovekeeper.domain.draft.model.DraftType;
import com.example.lovekeeper.domain.draft.repository.DraftRepository;
import com.example.lovekeeper.domain.member.exception.MemberErrorStatus;
import com.example.lovekeeper.domain.member.exception.MemberException;
import com.example.lovekeeper.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DraftQueryServiceImpl implements DraftQueryService {

	private final DraftRepository draftRepository;
	private final MemberRepository memberRepository;

	@Override
	public DraftResponse getDraft(Long memberId, int order, String draftType) {
		log.info("getDraft memberId: {}, order: {}", memberId, order);

		if (order < 1 || order > 4) {
			throw new DraftException(DraftErrorStatus.INVALID_ORDER);
		}

		// 현재 멤버 가져오기
		memberRepository.findById(memberId)
			.orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));

		Draft draft = draftRepository.findByMemberIdAndDraftOrderAndDraftType(memberId, order,
			DraftType.valueOf(draftType));

		if (draft == null) {
			throw new DraftException(DraftErrorStatus.DRAFT_NOT_FOUND);
		}

		log.info("getDraft success");

		return DraftResponse.of(draft);
	}
}
