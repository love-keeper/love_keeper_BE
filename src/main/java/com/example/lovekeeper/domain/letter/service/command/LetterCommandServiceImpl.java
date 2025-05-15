package com.example.lovekeeper.domain.letter.service.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.lovekeeper.domain.couple.exception.CoupleErrorStatus;
import com.example.lovekeeper.domain.couple.exception.CoupleException;
import com.example.lovekeeper.domain.couple.model.Couple;
import com.example.lovekeeper.domain.couple.repository.CoupleRepository;
import com.example.lovekeeper.domain.letter.model.Letter;
import com.example.lovekeeper.domain.letter.repository.LetterRepository;
import com.example.lovekeeper.domain.member.exception.MemberErrorStatus;
import com.example.lovekeeper.domain.member.exception.MemberException;
import com.example.lovekeeper.domain.member.model.Member;
import com.example.lovekeeper.domain.member.repository.MemberRepository;
import com.example.lovekeeper.global.infrastructure.service.fcm.FCMService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class LetterCommandServiceImpl implements LetterCommandService {

	private final MemberRepository memberRepository;
	private final LetterRepository letterRepository;
	private final CoupleRepository coupleRepository;

	private final FCMService fcmService;

	/**
	 * 편지 보내기
	 * @param senderId
	 * @param content
	 */
	@Override
	public void sendLetter(Long senderId, String content) {

		// 편지를 보내는 사람 (나 자신)
		Member sender = memberRepository.findById(senderId)
			.orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));

		// 편지를 받는 사람
		Couple currentCouple = coupleRepository.findByMemberId(senderId)
			.orElseThrow(() -> new CoupleException(CoupleErrorStatus.COUPLE_NOT_FOUND));

		// 상대방 파트너 멤버 찾기
		Member receiver;
		if (currentCouple.getMember1().equals(sender)) {
			receiver = currentCouple.getMember2();
		} else {
			receiver = currentCouple.getMember1();
		}

		// 편지 생성
		Letter letter = Letter.createLetter(currentCouple, sender, receiver, content);

		// 편지 저장
		Letter savedLetter = letterRepository.save(letter);

		// FCM 푸시 알림 전송
		fcmService.sendPushNotification(receiver.getId(),
			"새로운 편지가 도착했어요!",
			"편지함을 확인해주세요",
			System.currentTimeMillis(),
			savedLetter.getId() // 편지 ID 추가
		);
	}
}
