package com.example.lovekeeper.domain.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.lovekeeper.domain.auth.model.RefreshToken;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshToken, Long> {

}
