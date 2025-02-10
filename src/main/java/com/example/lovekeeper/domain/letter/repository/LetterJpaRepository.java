package com.example.lovekeeper.domain.letter.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.lovekeeper.domain.letter.dto.response.LetterCountByDateResponse;
import com.example.lovekeeper.domain.letter.model.Letter;
import com.example.lovekeeper.domain.member.model.Member;

public interface LetterJpaRepository extends JpaRepository<Letter, Long> {

	// 편지 목록을 보낸 사람(sender) 또는 받은 사람(receiver) 기준으로 조회
	Slice<Letter> findBySenderOrReceiver(Member sender, Member receiver, Pageable pageable);

	@Query(
		"SELECT new com.example.lovekeeper.domain.letter.dto.response.LetterCountByDateResponse(l.sentDate, COUNT(l)) "
			+
			"FROM Letter l " +
			"WHERE l.receiver.id = :memberId " +
			"AND l.sentDate BETWEEN :startDate AND :endDate " +
			"GROUP BY l.sentDate")
	List<LetterCountByDateResponse> countReceivedLettersByDate(LocalDate startDate, LocalDate endDate, Long memberId);

	@Query(
		"SELECT new com.example.lovekeeper.domain.letter.dto.response.LetterCountByDateResponse(l.sentDate, COUNT(l)) "
			+
			"FROM Letter l " +
			"WHERE l.sender.id = :memberId " +
			"AND l.sentDate BETWEEN :startDate AND :endDate " +
			"GROUP BY l.sentDate")
	List<LetterCountByDateResponse> countSentLettersByDate(LocalDate startDate, LocalDate endDate, Long memberId);

}
