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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "FCM", description = "푸시 알림 토큰 관리 및 알림 조회 API")
@RestController
@RequestMapping("/api/fcm")
@RequiredArgsConstructor
public class FCMController {

	private final FCMService fcmService;

	@Operation(summary = "FCM 토큰 등록",
		description = "인증된 사용자의 FCM 토큰을 등록합니다.",
		security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "FCM 토큰이 등록되었습니다.",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 토큰 형식",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class)))
	})
	@PostMapping("/token")
	public BaseResponse<String> registerToken(
		@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody @Valid FCMTokenRequest request) {
		fcmService.saveToken(userDetails.getMember().getId(), request.getToken());
		return BaseResponse.onSuccess("FCM 토큰이 등록되었습니다.");
	}

	@Operation(summary = "FCM 토큰 삭제",
		description = "특정 FCM 토큰을 삭제합니다. 인증이 필요하지 않습니다.",
		security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "FCM 토큰이 삭제되었습니다.",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 토큰 형식",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "404", description = "삭제하려는 토큰이 존재하지 않음",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class)))
	})
	@DeleteMapping("/token")
	public BaseResponse<String> removeToken(
		@RequestBody @Valid FCMTokenRequest request) {
		fcmService.removeToken(request.getToken());
		return BaseResponse.onSuccess("FCM 토큰이 삭제되었습니다.");
	}

	@Operation(summary = "푸시 알림 목록 조회",
		description = "인증된 사용자의 푸시 알림 목록을 페이지 단위로 조회합니다.",
		security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "푸시 알림 목록 조회 성공",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 페이지 요청",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class)))
	})
	@GetMapping("/notifications")
	public BaseResponse<PushNotificationListResponse> getPushNotificationList(
		@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
		@Parameter(description = "페이지 정보 (기본: size=10, sort=sentAt,desc)", hidden = true)
		@PageableDefault(size = 10, sort = "sentAt", direction = Sort.Direction.DESC) Pageable pageable) {
		PushNotificationListResponse response = fcmService.getPushNotificationList(
			userDetails.getMember().getId(), pageable);
		return BaseResponse.onSuccess(response);
	}

	@Operation(summary = "푸시 알림 읽음 처리",
		description = "인증된 사용자가 특정 푸시 알림을 읽음 처리합니다.",
		security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "알림을 읽었습니다.",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "404", description = "알림이 존재하지 않음",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class)))
	})
	@PostMapping("/read/{notificationId}")
	public BaseResponse<String> readPushNotification(
		@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
		@Parameter(description = "읽음 처리할 알림 ID", required = true, example = "1")
		@PathVariable Long notificationId) {
		fcmService.readPushNotification(userDetails.getMember().getId(), notificationId);
		return BaseResponse.onSuccess("알림을 읽었습니다.");
	}
}