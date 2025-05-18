package com.example.lovekeeper.domain.letter.api;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.lovekeeper.domain.letter.dto.request.SendLetterRequest;
import com.example.lovekeeper.domain.letter.dto.response.LetterResponse;
import com.example.lovekeeper.domain.letter.service.command.LetterCommandService;
import com.example.lovekeeper.domain.letter.service.query.LetterQueryService;
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

@Tag(name = "편지", description = "편지 작성 및 조회 API")
@Slf4j
@RestController
@RequestMapping("/api/letters")
@RequiredArgsConstructor
public class LetterController {

	private final LetterQueryService letterQueryService;
	private final LetterCommandService letterCommandService;

	@Operation(summary = "편지 목록 조회",
		description = "인증된 사용자의 편지 목록을 페이지 단위로 조회합니다. 무한 스크롤 방식으로 구현됩니다.",
		security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "편지 목록 조회 성공",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 페이지 요청 (page 또는 size가 유효하지 않음)",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class)))
	})
	@GetMapping("/list")
	public BaseResponse<LetterResponse.LetterListResponse> getLetters(
		@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
		@Parameter(description = "페이지 번호 (0부터 시작)", required = true, example = "0")
		@RequestParam int page,
		@Parameter(description = "페이지당 데이터 수", required = true, example = "10")
		@RequestParam int size) {
		return BaseResponse.onSuccess(letterQueryService.getLetters(userDetails.getMember().getId(), page, size));
	}

	@Operation(summary = "특정 편지 상세 조회",
		description = "인증된 사용자가 특정 편지의 상세 내용을 조회합니다.",
		security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "편지 상세 조회 성공",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "403", description = "해당 편지에 접근 권한 없음",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "404", description = "편지를 찾을 수 없음",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class)))
	})
	@GetMapping("/{letterId}")
	public BaseResponse<LetterResponse.LetterDetailResponse> getLetterDetail(
		@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
		@Parameter(description = "조회할 편지 ID", required = true, example = "1")
		@PathVariable Long letterId) {
		return BaseResponse.onSuccess(letterQueryService.getLetterDetail(userDetails.getMember().getId(), letterId));
	}

	@Operation(summary = "편지 보내기",
		description = "인증된 사용자가 편지를 작성하고 파트너에게 보냅니다.",
		security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "편지를 보냅니다.",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터 (내용이 비어 있음)",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "404", description = "커플 연결이 되어 있지 않음",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class)))
	})
	@PostMapping
	public BaseResponse<String> sendLetter(
		@RequestBody @Valid SendLetterRequest sendLetterRequest,
		@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
		log.info("편지를 보냅니다.");
		letterCommandService.sendLetter(userDetails.getMember().getId(), sendLetterRequest.getContent());
		return BaseResponse.onSuccess("편지를 보냅니다.");
	}

	@Operation(summary = "전체 편지 개수 조회",
		description = "인증된 사용자가 주고받은 전체 편지 개수를 조회합니다.",
		security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "전체 편지 개수 조회 성공",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class)))
	})
	@GetMapping("/count")
	public BaseResponse<Long> getLetterCount(
		@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
		return BaseResponse.onSuccess(letterQueryService.getLetterCount(userDetails.getMember().getId()));
	}

	@Operation(summary = "특정 날짜 편지 조회",
		description = "인증된 사용자의 특정 날짜에 주고받은 편지 목록을 페이지 단위로 조회합니다.",
		security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "특정 날짜 편지 조회 성공",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터 (날짜 형식 오류, 페이지 오류)",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class)))
	})
	@GetMapping("/by-date")
	public BaseResponse<LetterResponse.LetterListResponse> getLettersByDate(
		@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
		@Parameter(description = "조회할 날짜 (yyyy-MM-dd 형식)", required = true, example = "2025-02-13")
		@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") String date,
		@Parameter(description = "페이지 번호 (0부터 시작)", required = true, example = "0")
		@RequestParam int page,
		@Parameter(description = "페이지당 데이터 수", required = true, example = "10")
		@RequestParam int size) {
		return BaseResponse.onSuccess(
			letterQueryService.getLettersByDate(userDetails.getMember().getId(), date, page, size));
	}
}