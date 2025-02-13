package com.example.lovekeeper.global.infrastructure.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class S3Service {

	private final AmazonS3 amazonS3;

	@Value("${cloud.aws.s3.bucket}")
	private String bucketName;

	public String uploadProfileImage(MultipartFile file) throws IOException {
		// 파일명에 UUID를 추가하여 중복을 피하고 profileImage 폴더를 지정
		String fileName = "profileImage/" + UUID.randomUUID() + "-" + file.getOriginalFilename();

		// S3에 파일 업로드
		InputStream inputStream = file.getInputStream();
		amazonS3.putObject(new PutObjectRequest(bucketName, fileName, inputStream, null));

		// 업로드된 파일의 URL을 반환
		return amazonS3.getUrl(bucketName, fileName).toString();
	}
}
