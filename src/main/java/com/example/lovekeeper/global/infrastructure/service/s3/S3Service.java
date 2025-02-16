package com.example.lovekeeper.global.infrastructure.service.s3;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
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

	public String uploadProfileImage(MultipartFile file, String oldImageUrl) throws IOException {
		// 이전 이미지가 있다면 삭제
		if (oldImageUrl != null && !oldImageUrl.isEmpty()) {
			deleteProfileImage(oldImageUrl);
		}

		// 새 이미지 업로드
		String fileName = "profileImage/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
		InputStream inputStream = file.getInputStream();
		amazonS3.putObject(new PutObjectRequest(bucketName, fileName, inputStream, null));

		return amazonS3.getUrl(bucketName, fileName).toString();
	}

	private void deleteProfileImage(String imageUrl) {
		try {
			// URL에서 키 추출 (profileImage/UUID-filename 형식)
			String key = new java.net.URL(imageUrl).getPath().substring(1);
			amazonS3.deleteObject(bucketName, key);
		} catch (Exception e) {
			// URL 파싱 실패나 삭제 실패 시 로그만 남기고 계속 진행
			log.warn("Failed to delete old profile image: {}", imageUrl, e);
		}
	}
}
