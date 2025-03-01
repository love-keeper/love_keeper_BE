package com.example.lovekeeper.domain.couple.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.example.lovekeeper.domain.couple.exception.CoupleErrorStatus;
import com.example.lovekeeper.domain.couple.exception.CoupleException;
import com.example.lovekeeper.domain.letter.model.Letter;
import com.example.lovekeeper.domain.member.model.Member;
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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_1_id")
	private Member member1;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_2_id")
	private Member member2;

	@Builder.Default
	@OneToMany(mappedBy = "couple", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<Letter> letters = new ArrayList<>();

	@Builder.Default
	@OneToMany(mappedBy = "couple", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Promise> promises = new ArrayList<>();

	@Builder.Default
	private LocalDate startedAt = LocalDate.now();

	private LocalDate endedAt;

	@Builder.Default
	@Enumerated(EnumType.STRING)
	private CoupleStatus status = CoupleStatus.CONNECTED;

	@Builder.Default
	private int connectionCount = 1;

	@Builder.Default
	private int promiseCount = 0;

	@Builder.Default
	private boolean isPremium = false;

	//== 생성 메서드 ==//
	public static Couple connectCouple(Member member1, Member member2) {
		Couple couple = Couple.builder()
			.member1(member1)
			.member2(member2)
			.build();

		member1.getCouples1().add(couple);
		member2.getCouples2().add(couple);

		return couple;
	}

	//== 비즈니스 로직 ==//
	public void disconnect() {
		this.status = CoupleStatus.DISCONNECTED;
		this.endedAt = LocalDate.now();
	}

	public void updatePremium(boolean isPremium) {
		this.isPremium = isPremium;
	}

	public void increasePromiseCount() {
		this.promiseCount++;
	}

	//== 연관 관계 메서드 ==//

	public void reconnect() {
		this.status = CoupleStatus.CONNECTED;
		this.startedAt = LocalDate.now();
		this.endedAt = null;
		this.connectionCount++;
	}

	public boolean isActive() {
		return this.status == CoupleStatus.CONNECTED;
	}

	public boolean involves(Member member) {
		return member1.equals(member) || member2.equals(member);
	}

	public Member getPartner(Member member) {
		if (member1.equals(member))
			return member2;
		if (member2.equals(member))
			return member1;
		throw new CoupleException(CoupleErrorStatus.MEMBER_NOT_IN_COUPLE);
	}

	public void updateStartDate(LocalDate newStartDate) {
		this.startedAt = newStartDate;
	}
}
