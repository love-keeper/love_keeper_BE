package com.example.lovekeeper.global.infrastructure.service.fcm;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.lovekeeper.domain.fcm.model.FCMToken;
import com.example.lovekeeper.domain.fcm.repository.FCMTokenRepository;
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
@RequiredArgsConstructor
public class FCMServiceImpl implements FCMService {

	private final FCMTokenRepository fcmTokenRepository;
	private final MemberRepository memberRepository;

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
	public void sendPushNotification(Long memberId, String title, String body, Long timestamp) {
		List<FCMToken> tokens = fcmTokenRepository.findAllByMemberId(memberId);

		if (tokens.isEmpty()) {
			log.info("No FCM tokens found for member {}", memberId);
			return;
		}

		for (FCMToken token : tokens) {
			sendSingleNotification(token, memberId, title, body, System.currentTimeMillis());
		}
	}

	/**
	 * 단일 토큰에 대한 푸시 알림 전송
	 */
	private void sendSingleNotification(FCMToken token, Long memberId, String title, String body,
		Long letterTimestamp) {
		try {
			Message message = Message.builder()
				.setToken(token.getToken())
				.setNotification(Notification.builder()
					.setTitle(title)
					.setBody(body)
					.build())
				.putData("memberId", String.valueOf(memberId))
				.putData("time", String.valueOf(letterTimestamp))
				.build();

			String response = FirebaseMessaging.getInstance().send(message);
			log.info("Successfully sent message to token {}: {}", token.getToken(), response);

		} catch (FirebaseMessagingException e) {
			handleMessagingException(token, e);
		}
	}

	/**
	 * Firebase 메시징 예외 처리
	 */
	private void handleMessagingException(FCMToken token, FirebaseMessagingException e) {
		if (e.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED) {
			// 토큰이 더 이상 유효하지 않은 경우 삭제
			log.warn("Token {} is no longer valid, removing it", token.getToken());
			removeToken(token.getToken());
		} else {
			log.error("Failed to send message to token {}", token.getToken(), e);
		}
	}
}