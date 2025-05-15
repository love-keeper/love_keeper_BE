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
import com.example.lovekeeper.domain.couple.dto.response.CoupleInfoResponse;
import com.example.lovekeeper.domain.couple.dto.response.GenerateCodeResponse;
import com.example.lovekeeper.domain.couple.service.command.CoupleCommandService;
import com.example.lovekeeper.domain.couple.service.query.CoupleQueryService;
import com.example.lovekeeper.global.common.BaseResponse;
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

	@Operation(summary = "커플 정보 조회",
		description = "인증된 사용자가 속한 커플의 정보를 조회합니다.",
		security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "커플 정보 조회 성공",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "404", description = "커플 정보가 존재하지 않음",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class)))
	})
	@GetMapping("/info")
	public BaseResponse<CoupleInfoResponse> getCoupleInfo(
		@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
		return BaseResponse.onSuccess(coupleQueryService.getCoupleInfo(userDetails.getMember().getId()));
	}

	@Operation(summary = "초대 코드 생성",
		description = "인증된 사용자가 커플 연결을 위한 초대 코드를 생성합니다.",
		security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "초대 코드 생성 성공",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "409", description = "이미 커플에 연결된 사용자",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class)))
	})
	@PostMapping("/generate-code")
	public BaseResponse<GenerateCodeResponse> generateInviteCode(
		@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
		Long memberId = userDetails.getMember().getId();
		return BaseResponse.onSuccess(coupleCommandService.generateInviteCode(memberId));
	}

	@Operation(summary = "커플 연결",
		description = "초대 코드를 사용하여 인증된 사용자를 커플로 연결합니다.",
		security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "커플 연결이 완료되었습니다.",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 초대 코드",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "409", description = "이미 커플에 연결된 사용자",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class)))
	})
	@PostMapping("/connect")
	public BaseResponse<String> connectCouple(
		@RequestBody @Valid ConnectCoupleRequest request,
		@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
		Long memberId = userDetails.getMember().getId();
		coupleCommandService.connectCouple(memberId, request.getInviteCode());
		return BaseResponse.onSuccess("커플 연결이 완료되었습니다.");
	}

	@Operation(summary = "커플이 사귄 날짜 수 조회",
		description = "인증된 사용자가 속한 커플이 사귄 지 며칠이 지났는지 계산합니다.",
		security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "커플이 사귄 날짜 수 조회 성공",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "404", description = "커플 정보가 존재하지 않음",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class)))
	})
	@GetMapping("/days-since-started")
	public BaseResponse<Long> getDaysSinceStarted(
		@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
		return BaseResponse.onSuccess(
			coupleQueryService.getDaysSinceStarted(userDetails.getMember().getId()));
	}

	@Operation(summary = "커플 시작일 조회",
		description = "인증된 사용자가 속한 커플의 시작일을 조회합니다.",
		security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "커플 시작일 조회 성공",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "404", description = "커플 정보가 존재하지 않음",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class)))
	})
	@GetMapping("/start-date")
	public BaseResponse<LocalDate> getCoupleStartDate(
		@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
		return BaseResponse.onSuccess(coupleQueryService.getCoupleStartDate(userDetails.getMember().getId()));
	}

	@Operation(summary = "커플 시작일 수정",
		description = "인증된 사용자가 속한 커플의 시작일을 수정합니다.",
		security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "커플 시작일이 변경되었습니다.",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 날짜 형식",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "404", description = "커플 정보가 존재하지 않음",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class)))
	})
	@PutMapping("/start-date")
	public BaseResponse<String> updateCoupleStartDate(
		@RequestBody @Valid UpdateCoupleStartDateRequest request,
		@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
		coupleCommandService.updateCoupleStartDate(userDetails.getMember().getId(), request.getNewStartDate());
		return BaseResponse.onSuccess("커플 시작일이 변경되었습니다.");
	}

	@Operation(summary = "편지와 약속 개수 조회",
		description = "인증된 사용자가 속한 커플의 편지와 약속 개수를 조회합니다.",
		security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "편지와 약속 개수 조회 성공",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "404", description = "커플 정보가 존재하지 않음",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class)))
	})
	@GetMapping("/letters-promises/counts")
	public BaseResponse<CountResponse> getLettersAndPromisesCount(
		@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
		return BaseResponse.onSuccess(
			coupleQueryService.getLettersAndPromisesCount(userDetails.getMember().getId()));
	}

	@Operation(summary = "커플 연결 해제",
		description = "인증된 사용자의 커플 연결을 해제합니다.",
		security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "커플 연결이 해제되었습니다.",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "404", description = "커플 정보가 존재하지 않음",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class)))
	})
	@DeleteMapping
	public BaseResponse<String> disconnectCouple(
		@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
		coupleCommandService.disconnectCouple(userDetails.getMember().getId());
		return BaseResponse.onSuccess("커플 연결이 해제되었습니다.");
	}
}