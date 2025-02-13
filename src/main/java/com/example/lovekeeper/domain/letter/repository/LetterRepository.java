package com.example.lovekeeper.domain.letter.repository;

import java.time.LocalDate;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.lovekeeper.domain.letter.model.Letter;
import com.example.lovekeeper.domain.member.model.Member;

public interface LetterRepository extends JpaRepository<Letter, Long> {

	// 편지 목록을 보낸 사람(sender) 또는 받은 사람(receiver) 기준으로 조회
	Slice<Letter> findBySenderOrReceiver(Member sender, Member receiver, Pageable pageable);

	@Query("select count(l) from Letter l where l.couple.id = :coupleId")
	long countByCoupleId(@Param("coupleId") Long coupleId);

	Slice<Letter> findByCoupleIdAndSentDate(Long coupleId, LocalDate sentDate, Pageable pageable);
}
