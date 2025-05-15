package com.example.lovekeeper.global.infrastructure.service.s3;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

	private final S3Client s3Client;
	private final S3Presigner s3Presigner;

	@Value("${cloud.aws.s3.bucket}")
	private String bucketName;

	// 기존 메서드 유지
	public String uploadProfileImage(MultipartFile file, String oldImageUrl) throws IOException {
		log.info("[S3Service] MultipartFile upload start - fileName: {}", file.getOriginalFilename());
		long startTime = System.currentTimeMillis();

		if (oldImageUrl != null && !oldImageUrl.isEmpty()) {
			deleteProfileImage(oldImageUrl);
		}

		String fileName = "profileImage/" + UUID.randomUUID() + "-" + file.getOriginalFilename();

		PutObjectRequest putObjectRequest = PutObjectRequest.builder()
			.bucket(bucketName)
			.key(fileName)
			.build();

		s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

		GetUrlRequest getUrlRequest = GetUrlRequest.builder()
			.bucket(bucketName)
			.key(fileName)
			.build();

		URL url = s3Client.utilities().getUrl(getUrlRequest);
		String uploadedUrl = url.toString();

		long endTime = System.currentTimeMillis();
		long duration = endTime - startTime;
		log.info("[S3Service] MultipartFile upload end - fileName: {}, Duration: {}ms", fileName, duration);

		return uploadedUrl;
	}

	private void deleteProfileImage(String imageUrl) {
		try {
			String key = new java.net.URL(imageUrl).getPath().substring(1);

			DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
				.bucket(bucketName)
				.key(key)
				.build();

			s3Client.deleteObject(deleteRequest);
		} catch (Exception e) {
			log.warn("Failed to delete old profile image: {}", imageUrl, e);
		}
	}

	// Presigned URL 생성 메서드 추가
	public String generatePresignedUrl(String fileName) {
		log.info("[S3Service] Presigned URL generation start - fileName: {}", fileName);
		long startTime = System.currentTimeMillis();

		String uniqueFileName = "profileImage/" + UUID.randomUUID() + "-" + fileName;

		PutObjectRequest objectRequest = PutObjectRequest.builder()
			.bucket(bucketName)
			.key(uniqueFileName)
			.build();

		PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
			.signatureDuration(Duration.ofMinutes(10))
			.putObjectRequest(objectRequest)
			.build();

		PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
		String url = presignedRequest.url().toString();

		long endTime = System.currentTimeMillis();
		long duration = endTime - startTime;
		log.info("[S3Service] Presigned URL generation end - fileName: {}, Duration: {}ms", uniqueFileName, duration);

		return url;
	}

	// Presigned URL로 업로드 후 실제 URL 반환 (필요 시 사용)
	public String getFileUrl(String fileName) {
		GetUrlRequest getUrlRequest = GetUrlRequest.builder()
			.bucket(bucketName)
			.key(fileName)
			.build();

		URL url = s3Client.utilities().getUrl(getUrlRequest);
		return url.toString();
	}
}