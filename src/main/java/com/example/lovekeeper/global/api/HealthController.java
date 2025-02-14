package com.example.lovekeeper.global.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.lovekeeper.global.common.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Health Check", description = "서버 상태 체크 API")
@RestController
@RequiredArgsConstructor
public class HealthController {

	@Operation(summary = "서버 상태 확인", description = "서버가 정상적으로 동작하는지 확인합니다.")
	@GetMapping("/health")
	public ResponseEntity<BaseResponse<String>> checkHealth() {
		return ResponseEntity.ok(BaseResponse.onSuccess("Server is up and running"));
	}
}