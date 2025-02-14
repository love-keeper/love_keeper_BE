package com.example.lovekeeper.domain.fcm.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.lovekeeper.domain.fcm.model.FCMToken;

public interface FCMTokenRepository extends JpaRepository<FCMToken, Long> {
	Optional<FCMToken> findByMemberIdAndToken(Long memberId, String token);

	List<FCMToken> findAllByMemberId(Long memberId);

	void deleteByToken(String token);
}