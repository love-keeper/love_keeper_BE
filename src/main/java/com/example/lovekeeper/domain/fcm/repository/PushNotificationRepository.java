package com.example.lovekeeper.domain.fcm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.lovekeeper.domain.fcm.model.PushNotification;

public interface PushNotificationRepository extends JpaRepository<PushNotification, Long> {
	List<PushNotification> findAllByMemberId(Long memberId);

	List<PushNotification> findAllByMemberIdOrderBySentAtDesc(Long memberId); // 최신순 정렬
}