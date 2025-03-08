package com.example.lovekeeper.domain.fcm.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.lovekeeper.domain.fcm.model.PushNotification;

public interface PushNotificationRepository extends JpaRepository<PushNotification, Long> {
	Slice<PushNotification> findAllByMemberId(Long memberId, Pageable pageable);

	List<PushNotification> findAllByMemberIdOrderBySentAtDesc(Long memberId); // 최신순 정렬
}