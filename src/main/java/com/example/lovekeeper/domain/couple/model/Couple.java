package com.example.lovekeeper.domain.couple.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.example.lovekeeper.domain.letter.model.Letter;
import com.example.lovekeeper.domain.member.model.Member;
import com.example.lovekeeper.domain.note.model.Note;
import com.example.lovekeeper.domain.promise.model.Promise;
import com.example.lovekeeper.global.common.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
@Table(name = "couple")
public class Couple extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "couple_id")
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_1_id")
	private Member member1; // 멤버 1

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_2_id")
	private Member member2; // 멤버 2

	@Builder.Default
	@OneToMany(mappedBy = "couple", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<Letter> letters = new ArrayList<>(); // 편지

	@Builder.Default
	@OneToMany(mappedBy = "couple", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<Note> notes = new ArrayList<>();

	@Builder.Default
	@OneToMany(mappedBy = "couple", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Promise> promises = new ArrayList<>(); // 약속

	@Builder.Default
	private LocalDate startedAt = LocalDate.now(); // 시작일

	@Builder.Default
	@Enumerated(EnumType.STRING)
	private CoupleStatus status = CoupleStatus.CONNECTED; // 커플 상태

	//== 생성 메서드 ==//
	public static Couple connectCouple(Member currentMember, Member partnerMember) {
		return Couple.builder()
			.member1(currentMember)
			.member2(partnerMember)
			.build();
	}

	//== 비스니스 로직 ==//
	public void updateStartDate(LocalDate newStartDate) {
		this.startedAt = newStartDate;
	}

	public void addPromise(Promise promise) {
		this.promises.add(promise);
	}

}
