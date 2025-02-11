package com.example.lovekeeper.domain.couple.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.lovekeeper.domain.couple.model.Couple;

public interface CoupleRepository extends JpaRepository<Couple, Long> {

	@Query("SELECT c FROM Couple c WHERE c.member1.id = :memberId OR c.member2.id = :memberId")
	Optional<Couple> findByMemberId(@Param("memberId") Long memberId);

}

