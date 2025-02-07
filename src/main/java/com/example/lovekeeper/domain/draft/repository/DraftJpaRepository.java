package com.example.lovekeeper.domain.draft.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.lovekeeper.domain.draft.model.Draft;

public interface DraftJpaRepository extends JpaRepository<Draft, Long> {

	Draft findByMemberIdAndDraftOrder(Long memberId, int draftOrder);

}
