package com.example.lovekeeper.global.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class FCMConfig {

	@Value("${fcm.certification}")
	private String certificationPath;

	@PostConstruct
	public void initialize() {
		try {
			if (FirebaseApp.getApps().isEmpty()) {
				log.info("Initializing Firebase application");

				ClassPathResource resource = new ClassPathResource(certificationPath);

				FirebaseOptions options = FirebaseOptions.builder()
					.setCredentials(GoogleCredentials.fromStream(resource.getInputStream()))
					.build();

				FirebaseApp.initializeApp(options);
				log.info("Firebase application has been initialized successfully");
			}
		} catch (IOException e) {
			log.error("Failed to initialize Firebase application: {}", e.getMessage(), e);
			throw new RuntimeException("Failed to initialize Firebase", e);
		}
	}
}