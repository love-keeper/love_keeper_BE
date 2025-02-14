package com.example.lovekeeper.domain.fcm.api;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.lovekeeper.domain.fcm.dto.request.FCMTokenRequest;
import com.example.lovekeeper.global.common.BaseResponse;
import com.example.lovekeeper.global.infrastructure.service.fcm.FCMService;
import com.example.lovekeeper.global.security.user.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/fcm")
@RequiredArgsConstructor
public class FCMController {

	private final FCMService fcmService;

	@PostMapping("/token")
	@ResponseStatus(HttpStatus.OK)
	public BaseResponse<String> registerToken(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody FCMTokenRequest request
	) {
		fcmService.saveToken(userDetails.getMember().getId(), request.getToken());
		return BaseResponse.onSuccess("FCM 토큰이 등록되었습니다.");
	}

	@DeleteMapping("/token")
	@ResponseStatus(HttpStatus.OK)
	public BaseResponse<String> removeToken(
		@RequestBody FCMTokenRequest request
	) {
		fcmService.removeToken(request.getToken());
		return BaseResponse.onSuccess("FCM 토큰이 삭제되었습니다.");
	}
}