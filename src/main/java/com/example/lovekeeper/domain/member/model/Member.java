package com.example.lovekeeper.domain.member.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Where;

import com.example.lovekeeper.domain.couple.model.Couple;
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
@Where(clause = "deleted_at IS NULL")
public class Member extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	private Long id;

	@Builder.Default
	@OneToMany(mappedBy = "member1")
	private List<Couple> couples1 = new ArrayList<>();

	@Builder.Default
	@OneToMany(mappedBy = "member2")
	private List<Couple> couples2 = new ArrayList<>();

	@Builder.Default
	@OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Letter> sentLetters = new ArrayList<>();

	@Builder.Default
	@OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Letter> receivedLetters = new ArrayList<>();

	@Builder.Default
	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Draft> draft = new ArrayList<>();

	@Builder.Default
	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Promise> promises = new ArrayList<>();

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

	//==비즈니스 로직==//

	// 현재 활성화된 커플 관계 찾기
	public Optional<Couple> getActiveCouple() {
		return Stream.concat(couples1.stream(), couples2.stream())
			.filter(couple -> couple.getStatus() == CoupleStatus.CONNECTED)
			.findFirst();
	}

	// 특정 회원과의 이전 커플 관계 찾기
	public Optional<Couple> findPreviousCoupleWith(Member other) {
		return Stream.concat(couples1.stream(), couples2.stream())
			.filter(couple -> (couple.getMember1().equals(this) && couple.getMember2().equals(other)) ||
				(couple.getMember1().equals(other) && couple.getMember2().equals(this)))
			.sorted(Comparator.comparing(Couple::getStartedAt).reversed())
			.findFirst();
	}

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

	public void updateStatus(MemberStatus status) {
		this.status = status;
	}

	public void updateCoupleStatus(CoupleStatus coupleStatus) {
		this.coupleStatus = coupleStatus;
	}

	public void updateProfileImage(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}

	public void updateEmail(String email) {
		this.email = email;
	}

	public void updateProvider(Provider provider) {
		this.provider = provider;
	}

	public void updateProviderId(String providerId) {
		this.providerId = providerId;
	}

	public void updateRole(Role role) {
		this.role = role;
	}

	public void updateProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}

	public boolean isSocialMember() {
		return provider != Provider.LOCAL;
	}

	public void changeEmail(String email) {
		this.email = email;
	}
}