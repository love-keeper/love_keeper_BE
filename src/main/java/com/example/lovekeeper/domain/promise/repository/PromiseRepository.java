package com.example.lovekeeper.domain.promise.repository;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.lovekeeper.domain.promise.model.Promise;

public interface PromiseRepository extends JpaRepository<Promise, Long> {

	Slice<Promise> findByCoupleId(Long coupleId, Pageable pageable);

	Optional<Promise> findByIdAndCoupleId(Long promiseId, Long coupleId);
}
