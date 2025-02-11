package com.example.lovekeeper.domain.connectionhistory.model;

import java.time.LocalDate;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.example.lovekeeper.domain.couple.model.Couple;
import com.example.lovekeeper.domain.couple.model.CoupleStatus;
import com.example.lovekeeper.domain.member.model.Member;
import com.example.lovekeeper.global.common.BaseEntity;

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
@Table(name = "connection_history")
public class ConnectionHistory extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "connection_history_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_1_id")
	private Member member1; // 멤버 1

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_2_id")
	private Member member2; // 멤버 2

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "couple_id")
	private Couple couple;

	private LocalDate connectedAt; // 연결된 시간

	private LocalDate disconnectedAt; // 연결이 끊긴 시간

	private LocalDate reconnectedAt; // 재연결된 시간

	@Enumerated(EnumType.STRING)
	private CoupleStatus status; // 연결 상태 (연결중, 끊김)

	//== 생성 메서드 ==//
	public static ConnectionHistory makeHistory(Member currentMember, Member partnerMember, Couple couple) {

		ConnectionHistory newHistory = ConnectionHistory.builder()
			.member1(currentMember)
			.member2(partnerMember)
			.connectedAt(LocalDate.now())
			.couple(couple)
			.status(CoupleStatus.CONNECTED)
			.build();

		currentMember.getConnectionHistories1().add(newHistory);
		partnerMember.getConnectionHistories2().add(newHistory);

		return newHistory;
	}

}
