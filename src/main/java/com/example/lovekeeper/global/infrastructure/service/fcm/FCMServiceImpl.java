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

		fcmTokenRepository.findByMemberIdAndToken(memberId, token)
			.ifPresentOrElse(
				existingToken -> existingToken.updateToken(token),
				() -> fcmTokenRepository.save(FCMToken.builder()
					.member(member)
					.token(token)
					.build())
			);

		log.info("FCM token saved/updated for member: {}", memberId);
	}

	@Override
	@Transactional
	public void removeToken(String token) {
		fcmTokenRepository.deleteByToken(token);
		log.info("FCM token removed: {}", token);
	}

	@Override
	public void sendPushNotification(Long memberId, String title, String body, Long timestamp, Long letterId) {
		List<FCMToken> tokens = fcmTokenRepository.findAllByMemberId(memberId);

		if (tokens.isEmpty()) {
			log.info("No FCM tokens found for member {}", memberId);
			return;
		}

		for (FCMToken token : tokens) {
			sendSingleNotification(token, memberId, title, body, timestamp, letterId);
		}
	}

	private void sendSingleNotification(FCMToken token, Long memberId, String title, String body, Long timestamp,
		Long letterId) {
		try {
			Message message = Message.builder()
				.setToken(token.getToken())
				.setNotification(Notification.builder()
					.setTitle(title)
					.setBody(body)
					.build())
				.putData("memberId", String.valueOf(memberId))
				.putData("time", String.valueOf(timestamp))
				.putData("letterId", String.valueOf(letterId)) // 편지 ID 추가
				.build();

			String response = FirebaseMessaging.getInstance().send(message);
			log.info("Successfully sent message to token {}: {}", token.getToken(), response);

			Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));
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

	private void handleMessagingException(FCMToken token, FirebaseMessagingException e) {
		if (e.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED) {
			log.warn("Token {} is no longer valid, removing it", token.getToken());
			removeToken(token.getToken());
		} else {
			log.error("Failed to send message to token {}: {}", token.getToken(), e.getMessage());
			// 추가적인 디버깅 정보 출력
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

		// 지금까지 가져온 총 데이터 수 계산 (페이지 번호와 크기를 기반으로 단순 계산)
		long totalElementsFetched =
			(long)pageable.getPageNumber() * pageable.getPageSize() + responseSlice.getNumberOfElements();

		return PushNotificationListResponse.fromSlice(responseSlice, totalElementsFetched);
	}

	@Override
	public void readPushNotification(Long id, Long notificationId) {
		PushNotification notification = pushNotificationRepository.findById(notificationId)
			.orElseThrow(() -> new MemberException(MemberErrorStatus.NOTIFICATION_NOT_FOUND));

		if (!notification.getMember().getId().equals(id)) {
			throw new MemberException(MemberErrorStatus.NOTIFICATION_NOT_FOUND);
		}

		notification.read();
		pushNotificationRepository.save(notification);
	}

	// 상대 시간 계산 메서드
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
		} else if (days < 30) {
			return days + "일 전";
		} else if (days < 365) {
			long months = days / 30;
			return months + "개월 전";
		} else {
			long years = days / 365;
			return years + "년 전";
		}
	}
}