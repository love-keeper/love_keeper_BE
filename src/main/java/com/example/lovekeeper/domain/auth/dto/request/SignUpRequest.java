package com.example.lovekeeper.domain.auth.dto.request;

import java.time.LocalDate;

import org.springframework.web.multipart.MultipartFile;

import com.example.lovekeeper.domain.member.model.Provider;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SignUpRequest {

	private String email;

	private String password;

	private String nickname;

	private LocalDate birthDate;

	private MultipartFile profileImage;

	private Provider provider;

	private String providerId;

	public static SignUpRequest of(String email, String password, String nickname, LocalDate birthDate,
		MultipartFile profileImage, Provider provider, String providerId) {
		return SignUpRequest.builder()
			.email(email)
			.password(password)
			.nickname(nickname)
			.birthDate(birthDate)
			.profileImage(profileImage)
			.provider(provider)
			.providerId(providerId)
			.build();
	}

}