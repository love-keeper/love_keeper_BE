package com.example.lovekeeper.domain.promise.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.lovekeeper.domain.promise.model.Promise;

public interface PromiseRepository extends JpaRepository<Promise, Long> {

	Slice<Promise> findByCoupleId(Long coupleId, Pageable pageable);

	Optional<Promise> findByIdAndCoupleId(Long promiseId, Long coupleId);

	@Query("select count(p) from Promise p where p.couple.id = :coupleId")
	long countByCoupleId(@Param("coupleId") Long coupleId);

	// 연도/월로 조회하기 위한 메서드
	Slice<Promise> findByCoupleIdAndPromisedAt(Long coupleId, LocalDate promisedAt, Pageable pageable);
}
