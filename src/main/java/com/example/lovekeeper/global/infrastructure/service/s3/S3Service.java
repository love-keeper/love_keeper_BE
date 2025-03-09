package com.example.lovekeeper.global.infrastructure.service.s3;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

	private final AmazonS3 amazonS3;

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
		amazonS3.putObject(new PutObjectRequest(bucketName, fileName, file.getInputStream(), null));
		String uploadedUrl = amazonS3.getUrl(bucketName, fileName).toString();

		long endTime = System.currentTimeMillis();
		long duration = endTime - startTime;
		log.info("[S3Service] MultipartFile upload end - fileName: {}, Duration: {}ms", fileName, duration);

		return uploadedUrl;
	}

	private void deleteProfileImage(String imageUrl) {
		try {
			String key = new java.net.URL(imageUrl).getPath().substring(1);
			amazonS3.deleteObject(bucketName, key);
		} catch (Exception e) {
			log.warn("Failed to delete old profile image: {}", imageUrl, e);
		}
	}

	// Presigned URL 생성 메서드 추가
	public String generatePresignedUrl(String fileName) {
		log.info("[S3Service] Presigned URL generation start - fileName: {}", fileName);
		long startTime = System.currentTimeMillis();

		String uniqueFileName = "profileImage/" + UUID.randomUUID() + "-" + fileName;

		Date expiration = new Date();
		long expTimeMillis = expiration.getTime() + (10 * 60 * 1000); // 10분 후 만료
		expiration.setTime(expTimeMillis);

		GeneratePresignedUrlRequest generatePresignedUrlRequest =
			new GeneratePresignedUrlRequest(bucketName, uniqueFileName)
				.withMethod(HttpMethod.PUT)
				.withExpiration(expiration);

		URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);

		long endTime = System.currentTimeMillis();
		long duration = endTime - startTime;
		log.info("[S3Service] Presigned URL generation end - fileName: {}, Duration: {}ms", uniqueFileName, duration);

		return url.toString();
	}

	// Presigned URL로 업로드 후 실제 URL 반환 (필요 시 사용)
	public String getFileUrl(String fileName) {
		return amazonS3.getUrl(bucketName, fileName).toString();
	}
}