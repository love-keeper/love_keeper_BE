package com.example.lovekeeper.domain.couple.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.lovekeeper.domain.couple.dto.request.ConnectCoupleRequest;
import com.example.lovekeeper.domain.couple.dto.response.GenerateCodeResponse;
import com.example.lovekeeper.domain.couple.service.command.CoupleCommandService;
import com.example.lovekeeper.global.common.BaseResponse;
import com.example.lovekeeper.global.security.user.CustomUserDetails;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "커플", description = "커플 연결, 해제 등 커플 관련 API")
@Slf4j
@RestController
@RequestMapping("/api/couples")
@RequiredArgsConstructor
@Validated
public class CoupleController {

	private final CoupleCommandService coupleCommandService;

	/**
	 * 초대 코드 생성
	 * - RequestBody 없음 (JSON이 필요하면 DTO 추가)
	 * - 인증 사용자 ID를 @AuthenticationPrincipal로 추출
	 */
	@PostMapping("/generate-code")
	public BaseResponse<GenerateCodeResponse> generateInviteCode(
		@AuthenticationPrincipal CustomUserDetails userDetails) {

		Long memberId = userDetails.getMember().getId();
		return BaseResponse.onSuccess(coupleCommandService.generateInviteCode(memberId));
	}

	/**
	 * 초대 코드 연결
	 * - inviteCode는 요청 JSON
	 * - 인증 사용자 ID는 @AuthenticationPrincipal
	 */
	@PostMapping("/connect")
	public BaseResponse<String> connectCouple(
		@RequestBody ConnectCoupleRequest request,
		@AuthenticationPrincipal CustomUserDetails userDetails) {

		Long memberId = userDetails.getMember().getId();
		coupleCommandService.connectCouple(memberId, request.getInviteCode());
		return BaseResponse.onSuccess("커플 연결이 완료되었습니다.");
	}

}
