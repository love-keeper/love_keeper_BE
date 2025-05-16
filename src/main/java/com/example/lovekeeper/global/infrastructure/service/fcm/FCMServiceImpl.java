package com.example.lovekeeper.global.infrastructure.service.fcm;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.lovekeeper.domain.fcm.dto.response.PushNotificationListResponse;
import com.example.lovekeeper.domain.fcm.dto.response.PushNotificationResponse;
import com.example.lovekeeper.domain.fcm.model.FCMToken;
import com.example.lovekeeper.domain.fcm.model.PushNotification;
import com.example.lovekeeper.domain.fcm.repository.FCMTokenRepository;
import com.example.lovekeeper.domain.fcm.repository.PushNotificationRepository;
import com.example.lovekeeper.domain.member.exception.MemberErrorStatus;
import com.example.lovekeeper.domain.member.exception.MemberException;
import com.example.lovekeeper.domain.member.model.Member;
import com.example.lovekeeper.domain.member.repository.MemberRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.Notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FCMServiceImpl implements FCMService {

	private final FCMTokenRepository fcmTokenRepository;
	private final MemberRepository memberRepository;
	private final PushNotificationRepository pushNotificationRepository;

	@Override
	@Transactional
	public void saveToken(Long memberId, String token) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));

		// 해당 멤버의 토큰이 이미 존재하는지 확인
		if (fcmTokenRepository.findByMemberIdAndToken(memberId, token).isPresent()) {
			log.info("FCM token already exists for member: {} with token: {}", memberId, token);
			return; // 이미 존재하면 저장하지 않고 종료
		}

		// 토큰이 존재하지 않으면 새로 저장
		fcmTokenRepository.save(FCMToken.builder()
			.member(member)
			.token(token)
			.build());

		log.info("FCM token saved for member: {} with token: {}", memberId, token);
	}

	@Override
	@Transactional
	public void removeToken(String token) {
		fcmTokenRepository.deleteByToken(token);
		log.info("FCM token removed: {}", token);
	}

	@Override
	@Transactional // 트랜잭션 추가
	public void sendPushNotification(Long memberId, Long letterId, String title, String body, Long timestamp,
		Long entityId) {
		List<FCMToken> tokens = fcmTokenRepository.findAllByMemberId(memberId);

		if (tokens.isEmpty()) {
			log.info("No FCM tokens found for member {}", memberId);
			return;
		}

		// Member를 한 번만 조회하여 재사용
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));

		for (FCMToken token : tokens) {
			sendSingleNotification(token, letterId, member, title, body, timestamp, entityId);
		}
	}

	private void sendSingleNotification(FCMToken token, Long letterId, Member member, String title, String body,
		Long timestamp,
		Long entityId) {
		try {
			Message message = Message.builder()
				.setToken(token.getToken())
				.setNotification(Notification.builder()
					.setTitle(title)
					.setBody(body)
					.build())
				.putData("memberId", String.valueOf(member.getId()))
				.putData("letterId", String.valueOf(letterId))
				.putData("time", String.valueOf(timestamp))
				.putData("entityId", String.valueOf(entityId))
				.putData("type", determineNotificationType(title, entityId)) // 타입 결정 로직 분리
				.build();

			String response = FirebaseMessaging.getInstance().send(message);
			log.info("Successfully sent message to token {}: {}", token.getToken(), response);

			// Member 객체를 재사용
			PushNotification notification = PushNotification.builder()
				.member(member)
				.title(title)
				.body(body)
				.sentAt(LocalDateTime.now())
				.build();
			pushNotificationRepository.save(notification);

		} catch (FirebaseMessagingException e) {
			log.error("Failed to send message to token {}: {}", token.getToken(), e.getMessage());
			handleMessagingException(token, e);
		} catch (Exception e) {
			log.error("Unexpected error while sending notification to token {}: {}", token.getToken(), e.getMessage());
		}
	}

	// 타입 결정 로직을 별도 메서드로 분리
	private String determineNotificationType(String title, Long entityId) {
		if (entityId != null && title.contains("약속")) {
			return "promise";
		}
		return "letter";
	}

	private void handleMessagingException(FCMToken token, FirebaseMessagingException e) {
		if (e.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED) {
			log.warn("Token {} is no longer valid, removing it", token.getToken());
			removeToken(token.getToken());
		} else {
			log.error("Failed to send message to token {}: {}", token.getToken(), e.getMessage());
			log.error("Error details: {}", e.getCause() != null ? e.getCause().getMessage() : "No cause available");
		}
	}

	@Override
	@Transactional(readOnly = true)
	public PushNotificationListResponse getPushNotificationList(Long memberId, Pageable pageable) {
		Slice<PushNotification> notifications = pushNotificationRepository.findAllByMemberId(memberId, pageable);
		Slice<PushNotificationResponse> responseSlice = notifications.map(notif -> PushNotificationResponse.of(
			notif.getId(),
			notif.getTitle(),
			notif.getBody(),
			calculateRelativeTime(notif.getSentAt()),
			notif.isRead()
		));

		long totalElementsFetched =
			(long)pageable.getPageNumber() * pageable.getPageSize() + responseSlice.getNumberOfElements();

		return PushNotificationListResponse.fromSlice(responseSlice, totalElementsFetched);
	}

	@Override
	@Transactional
	public void readPushNotification(Long id, Long notificationId) {
		PushNotification notification = pushNotificationRepository.findById(notificationId)
			.orElseThrow(() -> new MemberException(MemberErrorStatus.NOTIFICATION_NOT_FOUND));

		if (!notification.getMember().getId().equals(id)) {
			throw new MemberException(MemberErrorStatus.NOTIFICATION_NOT_FOUND);
		}

		notification.read();
		pushNotificationRepository.save(notification);
	}

	// 더 정확한 상대 시간 계산 메서드
	private String calculateRelativeTime(LocalDateTime sentAt) {
		LocalDateTime now = LocalDateTime.now();
		Duration duration = Duration.between(sentAt, now);

		long seconds = duration.getSeconds();
		long minutes = seconds / 60;
		long hours = minutes / 60;
		long days = hours / 24;

		if (seconds < 60) {
			return seconds + "초 전";
		} else if (minutes < 60) {
			return minutes + "분 전";
		} else if (hours < 24) {
			return hours + "시간 전";
		} else if (days < 7) {
			return days + "일 전";
		} else if (days < 30) {
			long weeks = days / 7;
			return weeks + "주 전";
		} else {
			// Period를 사용하여 더 정확한 월/년 계산
			java.time.Period period = java.time.Period.between(sentAt.toLocalDate(), now.toLocalDate());

			if (period.getYears() > 0) {
				return period.getYears() + "년 전";
			} else if (period.getMonths() > 0) {
				return period.getMonths() + "개월 전";
			} else {
				return days + "일 전";
			}
		}
	}
}