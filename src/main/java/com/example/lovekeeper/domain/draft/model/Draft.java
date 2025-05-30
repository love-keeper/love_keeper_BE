package com.example.lovekeeper.domain.draft.model;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

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
@Table(name = "draft")
public class Draft extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "draft_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	private int draftOrder;

	@Enumerated(EnumType.STRING)
	private DraftType draftType;

	@Lob
	private String content;

	//== 생성 메서드 ==//
	public static Draft createDraft(Member member, int order, String content, DraftType draftType) {
		return Draft.builder()
			.member(member)
			.draftOrder(order)
			.content(content)
			.draftType(draftType)
			.build();
	}

	//== 연관 관계 메서드 ==//

	//== 비즈니스 로직 ==//
	public void updateContent(String content) {
		this.content = content;
	}

}
