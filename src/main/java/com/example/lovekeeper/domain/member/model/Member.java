package com.example.lovekeeper.domain.member.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.example.lovekeeper.domain.connectionhistory.model.ConnectionHistory;
import com.example.lovekeeper.domain.couple.model.CoupleStatus;
import com.example.lovekeeper.domain.draft.model.Draft;
import com.example.lovekeeper.domain.letter.model.Letter;
import com.example.lovekeeper.domain.promise.model.Promise;
import com.example.lovekeeper.global.common.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "member")
public class Member extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	private Long id;

	@Builder.Default
	@OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Letter> sentLetters = new ArrayList<>();

	@Builder.Default
	@OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Letter> receivedLetters = new ArrayList<>();

	@OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
	private Draft draft;

	@Builder.Default
	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Promise> promises = new ArrayList<>();

	@Builder.Default
	@OneToMany(mappedBy = "member1")
	private List<ConnectionHistory> connectionHistories1 = new ArrayList<>();

	@Builder.Default
	@OneToMany(mappedBy = "member2")
	private List<ConnectionHistory> connectionHistories2 = new ArrayList<>();

	private String inviteCode;

	private String email;

	private String password;

	private String nickname;

	private String profileImageUrl;

	private LocalDate birthDay;

	@Builder.Default
	@Enumerated(EnumType.STRING)
	private MemberStatus status = MemberStatus.ACTIVE;

	@Builder.Default
	@Enumerated(EnumType.STRING)
	private CoupleStatus coupleStatus = CoupleStatus.DISCONNECTED;

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
			.birthDay(birthDate)
			.profileImageUrl(profileImageUrl)
			.provider(provider)
			.build();
	}

	public static Member createSocialMember(String email, String nickname, LocalDate birthDate, String profileImageUrl,
		Provider provider, String providerId) {
		return Member.builder()
			.email(email)
			.nickname(nickname)
			.birthDay(birthDate)
			.profileImageUrl(profileImageUrl)
			.provider(provider)
			.providerId(providerId)
			.build();
	}

	//==연관관계 메서드==//

	//==비즈니스 로직==//

	public void updateInviteCode(String inviteCode) {
		this.inviteCode = inviteCode;
	}

	public void changeNickname(String nickname) {
		this.nickname = nickname;
	}

	public void changeBirthday(LocalDate birthday) {
		this.birthDay = birthday;
	}

	public void changePassword(String newPassword) {
		this.password = newPassword;
	}
}
