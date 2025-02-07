package com.example.lovekeeper.domain.draft.service.query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.lovekeeper.domain.draft.dto.response.DraftResponse;
import com.example.lovekeeper.domain.draft.exception.DraftErrorStatus;
import com.example.lovekeeper.domain.draft.exception.DraftException;
import com.example.lovekeeper.domain.draft.model.Draft;
import com.example.lovekeeper.domain.draft.repository.DraftJpaRepository;
import com.example.lovekeeper.domain.member.exception.MemberErrorStatus;
import com.example.lovekeeper.domain.member.exception.MemberException;
import com.example.lovekeeper.domain.member.repository.MemberJpaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DraftQueryServiceImpl implements DraftQueryService {

	private final DraftJpaRepository draftJpaRepository;
	private final MemberJpaRepository memberJpaRepository;

	@Override
	public DraftResponse getDraft(Long memberId, int order) {
		log.info("getDraft memberId: {}, order: {}", memberId, order);

		if (order < 1 || order > 4) {
			throw new DraftException(DraftErrorStatus.INVALID_ORDER);
		}

		// 현재 멤버 가져오기
		memberJpaRepository.findById(memberId)
			.orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));

		Draft draft = draftJpaRepository.findByMemberIdAndDraftOrder(memberId, order);

		if (draft == null) {
			throw new DraftException(DraftErrorStatus.DRAFT_NOT_FOUND);
		}

		log.info("getDraft success");

		return DraftResponse.of(draft);
	}
}
