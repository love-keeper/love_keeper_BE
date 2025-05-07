package com.example.lovekeeper.global.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import jakarta.annotation.PostConstruct;

@Configuration
public class FCMConfig {

	@Value("${cloud.aws.region.static}")
	private String region;

	@Value("${fcm.secret-name}")
	private String secretName;

	@PostConstruct
	public void initialize() throws IOException {
		if (FirebaseApp.getApps().isEmpty()) {
			// AWS SDK 클라이언트 생성
			AWSSecretsManager client = AWSSecretsManagerClientBuilder.standard()
				.withRegion(region)
				.build();

			GetSecretValueRequest request = new GetSecretValueRequest()
				.withSecretId(secretName);

			GetSecretValueResult result = client.getSecretValue(request);
			String credentials = result.getSecretString();

			FirebaseOptions options = FirebaseOptions.builder()
				.setCredentials(GoogleCredentials.fromStream(
					new ByteArrayInputStream(credentials.getBytes())))
				.build();

			FirebaseApp.initializeApp(options);
		}
	}
}