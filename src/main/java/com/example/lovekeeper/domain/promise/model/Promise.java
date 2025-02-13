package com.example.lovekeeper.domain.promise.model;

import java.time.LocalDate;

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
@Table(name = "promise") // 약속
public class Promise extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "promise_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "couple_id")
	private Couple couple;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	private String content;

	@Builder.Default
	private LocalDate promisedAt = LocalDate.now();

	//== 생성 메서드 ==//
	public static Promise createPromise(Couple couple, Member member, String content) {
		Promise promise = Promise.builder()
			.couple(couple)
			.member(member)
			.content(content)
			.build();

		// 연관 관계 설정
		couple.getPromises().add(promise); // Couple 객체의 addPromise 메서드를 호출하여 연관 관계 설정
		member.getPromises().add(promise); // Member 객체의 promises에 추가

		return promise;
	}

}
