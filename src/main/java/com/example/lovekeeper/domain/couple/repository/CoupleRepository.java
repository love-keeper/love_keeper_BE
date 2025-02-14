package com.example.lovekeeper.domain.couple.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.lovekeeper.domain.couple.model.Couple;
import com.example.lovekeeper.domain.couple.model.CoupleStatus;

public interface CoupleRepository extends JpaRepository<Couple, Long> {

	@Query("SELECT c FROM Couple c WHERE (c.member1.id = :memberId OR c.member2.id = :memberId) AND c.status = 'CONNECTED'")
	Optional<Couple> findByMemberId(@Param("memberId") Long memberId);

	@Query("SELECT c FROM Couple c WHERE " +
		"(c.member1.id = :member1Id AND c.member2.id = :member2Id) OR " +
		"(c.member1.id = :member2Id AND c.member2.id = :member1Id) " +
		"ORDER BY c.startedAt DESC")
	Optional<Couple> findPreviousCouple(
		@Param("member1Id") Long member1Id,
		@Param("member2Id") Long member2Id
	);

	// 삭제 대상 커플 조회를 위한 메서드 추가
	List<Couple> findByStatusAndEndedAtBefore(CoupleStatus status, LocalDate date);

}

