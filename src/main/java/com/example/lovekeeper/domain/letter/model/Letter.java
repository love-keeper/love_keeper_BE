package com.example.lovekeeper.domain.letter.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.example.lovekeeper.domain.couple.model.Couple;
import com.example.lovekeeper.domain.member.model.Member;
import com.example.lovekeeper.global.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@DynamicUpdate
@DynamicInsert
@Table(name = "letter")
public class Letter extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "letter_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "couple_id")
	private Couple couple;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sender_id")
	private Member sender;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "receiver_id")
	private Member receiver;

	@Lob
	private String content;

	@Builder.Default
	private LocalDateTime sentDate = LocalDateTime.now();

	//== 생성 메서드 ==//
	public static Letter createLetter(Couple couple, Member sender,
		Member receiver, String content) {

		Letter newLetter = Letter.builder()
			.couple(couple)
			.sender(sender)
			.receiver(receiver)
			.content(content)
			.build();

		couple.getLetters().add(newLetter);
		sender.getSentLetters().add(newLetter);
		receiver.getReceivedLetters().add(newLetter);

		return newLetter;

	}

	//== 비즈니스 로직 ==//
	public void send() {
		// Send logic if required (e.g. notifications, etc.)
	}

}
