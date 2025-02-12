package com.example.lovekeeper.domain.promise.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.lovekeeper.domain.promise.model.Promise;

public interface PromiseRepository extends JpaRepository<Promise, Long> {
}
