package com.example.lovekeeper.global.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import jakarta.annotation.PostConstruct;

@Configuration
public class FCMConfig {

	@Value("${fcm.certification}")
	private String googleApplicationCredentials;

	@PostConstruct
	public void initialize() throws IOException {
		// Firebase가 이미 초기화되어 있는지 확인
		if (FirebaseApp.getApps().isEmpty()) {
			ClassPathResource resource = new ClassPathResource(googleApplicationCredentials);

			FirebaseOptions options = FirebaseOptions.builder()
				.setCredentials(GoogleCredentials.fromStream(resource.getInputStream()))
				.build();

			FirebaseApp.initializeApp(options);
		}
	}
}