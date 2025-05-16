package com.example.lovekeeper.global.infrastructure.service.fcm;

import org.springframework.data.domain.Pageable;

import com.example.lovekeeper.domain.fcm.dto.response.PushNotificationListResponse;

public interface FCMService {
	/**
	 * FCM 토큰 저장
	 * @param memberId 회원 ID
	 * @param token FCM 토큰
	 */
	void saveToken(Long memberId, String token);

	/**
	 * FCM 토큰 삭제
	 * @param token 삭제할 FCM 토큰
	 */
	void removeToken(String token);

	/**
	 * 푸시 알림 전송
	 * @param memberId 수신할 회원 ID
	 * @param title 알림 제목
	 * @param body 알림 내용
	 */
	void sendPushNotification(Long memberId, Long letterId, String title, String body, Long timestamp, Long entityId);

	/**
	 * 푸시 알림 리스트 가져오기
	 */
	PushNotificationListResponse getPushNotificationList(Long memberId, Pageable pageable);

	void readPushNotification(Long id, Long notificationId);

}