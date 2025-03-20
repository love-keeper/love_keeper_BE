package com.example.lovekeeper.domain.draft.service.command;

import com.example.lovekeeper.domain.draft.dto.request.SaveDraftRequest;

public interface DraftCommandService {

	/**
	 * 편지를 임시 저장
	 * @param memberId 멤버 ID
	 * @param request 임시 저장할 편지 정보
	 */
	void saveDraft(Long memberId, SaveDraftRequest request);

	void deleteDraft(Long memberId, int order, String draftType);
}


