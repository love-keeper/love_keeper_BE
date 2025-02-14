package com.example.lovekeeper.domain.couple.api;

import java.time.LocalDate;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.lovekeeper.domain.couple.dto.request.ConnectCoupleRequest;
import com.example.lovekeeper.domain.couple.dto.request.UpdateCoupleStartDateRequest;
import com.example.lovekeeper.domain.couple.dto.response.CountResponse;
import com.example.lovekeeper.domain.couple.dto.response.GenerateCodeResponse;
import com.example.lovekeeper.domain.couple.service.command.CoupleCommandService;
import com.example.lovekeeper.domain.couple.service.query.CoupleQueryService;
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

	private final CoupleQueryService coupleQueryService;
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

	/**
	 * 인증된 사용자가 속한 커플이 사귄 지 며칠이 지났는지 계산
	 * @return 커플이 사귄 날짜
	 */
	@GetMapping("/days-since-started")
	public BaseResponse<Long> getDaysSinceStarted(@AuthenticationPrincipal CustomUserDetails userDetails) {
		return BaseResponse.onSuccess(
			coupleQueryService.getDaysSinceStarted(userDetails.getMember().getId()));
	}

	/**
	 * 커플의 시작일 조회
	 */
	@GetMapping("/start-date")
	public BaseResponse<LocalDate> getCoupleStartDate(@AuthenticationPrincipal CustomUserDetails userDetails) {
		return BaseResponse.onSuccess(coupleQueryService.getCoupleStartDate(userDetails.getMember().getId()));
	}

	/**
	 * 커플의 시작일 수정
	 * @param request 시작일 수정 정보
	 * @param userDetails 인증된 사용자 정보
	 * @return 성공 메시지
	 */
	@PutMapping("/start-date")
	public BaseResponse<String> updateCoupleStartDate(
		@RequestBody UpdateCoupleStartDateRequest request,
		@AuthenticationPrincipal CustomUserDetails userDetails) {

		coupleCommandService.updateCoupleStartDate(userDetails.getMember().getId(), request.getNewStartDate());

		return BaseResponse.onSuccess("커플 시작일이 변경되었습니다.");
	}

	@GetMapping("/letters-promises/counts")
	public BaseResponse<CountResponse> getLettersAndPromisesCount(
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		return BaseResponse.onSuccess(
			coupleQueryService.getLettersAndPromisesCount(userDetails.getMember().getId())
		);
	}

	@DeleteMapping
	public BaseResponse<String> disconnectCouple(
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		coupleCommandService.disconnectCouple(userDetails.getMember().getId());
		return BaseResponse.onSuccess("커플 연결이 해제되었습니다.");
	}
}

