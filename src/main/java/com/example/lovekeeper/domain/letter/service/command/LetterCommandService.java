package com.example.lovekeeper.domain.letter.service.command;

public interface LetterCommandService {

	/**
	 * 편지 보내기
	 * @param senderId
	 * @param content
	 */
	void sendLetter(Long senderId, String content);

}
