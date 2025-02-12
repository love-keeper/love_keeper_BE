package com.example.lovekeeper.domain.letter.service.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.lovekeeper.domain.letter.repository.LetterRepository;
import com.example.lovekeeper.domain.member.exception.MemberErrorStatus;
import com.example.lovekeeper.domain.member.exception.MemberException;
import com.example.lovekeeper.domain.member.model.Member;
import com.example.lovekeeper.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class LetterCommandServiceImpl implements LetterCommandService {

	private final MemberRepository memberRepository;
	private final LetterRepository letterRepository;

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

	}
}
