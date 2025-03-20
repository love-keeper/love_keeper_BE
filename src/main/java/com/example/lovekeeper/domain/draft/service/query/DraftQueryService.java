package com.example.lovekeeper.domain.draft.service.query;

import com.example.lovekeeper.domain.draft.dto.response.DraftResponse;

public interface DraftQueryService {

	DraftResponse getDraft(Long memberId, int order, String draftType);

}
