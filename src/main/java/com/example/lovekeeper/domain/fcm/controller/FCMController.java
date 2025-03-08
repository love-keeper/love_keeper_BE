package com.example.lovekeeper.domain.fcm.api;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.lovekeeper.domain.fcm.dto.request.FCMTokenRequest;
import com.example.lovekeeper.domain.fcm.dto.response.PushNotificationListResponse;
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
	public BaseResponse<String> registerToken(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody FCMTokenRequest request
	) {
		fcmService.saveToken(userDetails.getMember().getId(), request.getToken());
		return BaseResponse.onSuccess("FCM 토큰이 등록되었습니다.");
	}

	@DeleteMapping("/token")
	public BaseResponse<String> removeToken(
		@RequestBody FCMTokenRequest request
	) {
		fcmService.removeToken(request.getToken());
		return BaseResponse.onSuccess("FCM 토큰이 삭제되었습니다.");
	}

	@GetMapping("/notifications")
	public BaseResponse<PushNotificationListResponse> getPushNotificationList(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PageableDefault(size = 10, sort = "sentAt", direction = Sort.Direction.DESC) Pageable pageable
	) {
		PushNotificationListResponse response = fcmService.getPushNotificationList(
			userDetails.getMember().getId(), pageable);
		return BaseResponse.onSuccess(response);
	}

	@PostMapping("/read/{notificationId}")
	public BaseResponse<String> readPushNotification(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable Long notificationId

	) {
		fcmService.readPushNotification(userDetails.getMember().getId(), notificationId);
		return BaseResponse.onSuccess("알림을 읽었습니다.");
	}

}