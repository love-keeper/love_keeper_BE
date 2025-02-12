package com.example.lovekeeper.domain.draft.service.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.lovekeeper.domain.draft.dto.request.SaveDraftRequest;
import com.example.lovekeeper.domain.draft.model.Draft;
import com.example.lovekeeper.domain.draft.repository.DraftRepository;
import com.example.lovekeeper.domain.member.exception.MemberErrorStatus;
import com.example.lovekeeper.domain.member.exception.MemberException;
import com.example.lovekeeper.domain.member.model.Member;
import com.example.lovekeeper.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DraftCommandServiceImpl implements DraftCommandService {

	private final DraftRepository draftRepository;
	private final MemberRepository memberRepository;

	/**
	 * 편지를 임시 저장
	 * @param memberId 멤버 ID
	 * @param request 임시 저장할 편지 정보
	 */
	@Override
	public void saveDraft(Long memberId, SaveDraftRequest request) {
		log.info("saveDraft memberId: {}, request: {}", memberId, request);

		// 현재 멤버 가져오기
		Member currentMember = memberRepository.findById(memberId)
			.orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));

		// 저장되어 있는 임시 저장된 편지를 가져옴.
		Draft existingDraft = draftRepository.findByMemberIdAndDraftOrder(memberId, request.getDraftOrder());

		// 임시 저장된 편지가 있으면 업데이트, 없으면 새로 생성 및 저장.
		if (existingDraft != null) {
			existingDraft.updateContent(request.getContent());
		} else {
			draftRepository.save(Draft.createDraft(currentMember, request.getDraftOrder(), request.getContent()));
		}

		log.info("saveDraft success");

	}
}
