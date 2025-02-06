package com.example.lovekeeper.domain.member.model;

import java.time.LocalDate;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.example.lovekeeper.domain.auth.model.RefreshToken;
import com.example.lovekeeper.domain.couple.model.Couple;
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
@Table(name = "member")
public class Member extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "couple_id")
	private Couple couple;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "partner_id")
	private Member partner;

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
	private RefreshToken refreshToken;

	private String inviteCode;

	private String email;

	private String password;

	private String nickname;

	private String profileImageUrl;

	private String partnerName;

	private LocalDate birthDate;

	@Builder.Default
	@Enumerated(EnumType.STRING)
	private Status status = Status.ACTIVE;

	@Builder.Default
	@Enumerated(EnumType.STRING)
	private Role role = Role.ROLE_USER;

	@Enumerated(EnumType.STRING)
	private Provider provider;

	private String providerId;

	//==생성 메서드==//
	public static Member createLocalMember(String email, String password, String nickname, LocalDate birthDate,
		String profileImageUrl, Provider provider, String providerId) {
		return Member.builder()
			.email(email)
			.password(password)
			.nickname(nickname)
			.birthDate(birthDate)
			.profileImageUrl(profileImageUrl)
			.provider(provider)
			.build();
	}

	public static Member createSocialMember(String email, String nickname, LocalDate birthDate, String profileImageUrl,
		Provider provider, String providerId) {
		return Member.builder()
			.email(email)
			.nickname(nickname)
			.birthDate(birthDate)
			.profileImageUrl(profileImageUrl)
			.provider(provider)
			.providerId(providerId)
			.build();
	}

	//==연관관계 메서드==//
	public boolean isActive() {
		return this.status == Status.ACTIVE;
	}
}
