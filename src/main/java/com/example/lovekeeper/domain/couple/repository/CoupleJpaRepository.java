package com.example.lovekeeper.domain.couple.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.lovekeeper.domain.couple.model.Couple;

public interface CoupleJpaRepository extends JpaRepository<Couple, Long> {
}
