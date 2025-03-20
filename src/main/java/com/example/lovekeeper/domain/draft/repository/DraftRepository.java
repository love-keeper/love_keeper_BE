package com.example.lovekeeper.domain.draft.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.lovekeeper.domain.draft.model.Draft;
import com.example.lovekeeper.domain.draft.model.DraftType;

public interface DraftRepository extends JpaRepository<Draft, Long> {

	Draft findByMemberIdAndDraftOrderAndDraftType(Long memberId, int draftOrder, DraftType draftType);

}
