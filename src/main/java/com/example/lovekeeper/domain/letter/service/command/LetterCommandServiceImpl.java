package com.example.lovekeeper.domain.letter.service.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.lovekeeper.domain.letter.model.Letter;
import com.example.lovekeeper.domain.letter.repository.LetterJpaRepository;
import com.example.lovekeeper.domain.member.exception.MemberErrorStatus;
import com.example.lovekeeper.domain.member.exception.MemberException;
import com.example.lovekeeper.domain.member.model.Member;
import com.example.lovekeeper.domain.member.repository.MemberJpaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class LetterCommandServiceImpl implements LetterCommandService {

	private final MemberJpaRepository memberJpaRepository;
	private final LetterJpaRepository letterJpaRepository;

	/**
	 * 편지 보내기
	 * @param senderId
	 * @param content
	 */
	@Override
	public void sendLetter(Long senderId, String content) {

		// 편지를 보내는 사람 (나 자신)
		Member sender = memberJpaRepository.findById(senderId)
			.orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));

		log.info("편지를 보냅니다. senderId: {}, content: {}", senderId, content);

		// 편지를 받는 사람 (커플)
		Member receiver = sender.getPartner();

		// 커플이 없는 경우
		if (receiver == null) {
			throw new MemberException(MemberErrorStatus.PARTNER_NOT_FOUND);
		}

		// 편지 객체 생성
		Letter letter = Letter.createLetter(sender, receiver, content);

		// 편지 저장
		letterJpaRepository.save(letter);

		// 편지 전송 (알림 등 추가 로직이 있을 경우 여기에 작성)
		letter.send();

	}
}
