package com.example.lovekeeper.domain.promise.api;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.lovekeeper.domain.promise.dto.request.PromiseRequest;
import com.example.lovekeeper.domain.promise.dto.response.PromiseResponse;
import com.example.lovekeeper.domain.promise.service.command.PromiseCommandService;
import com.example.lovekeeper.domain.promise.service.query.PromiseQueryService;
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

@Tag(name = "약속", description = "약속 API")
@Slf4j
@RestController
@RequestMapping("/api/promises")
@RequiredArgsConstructor
public class PromiseController {

	private final PromiseQueryService promiseQueryService;
	private final PromiseCommandService promiseCommandService;

	@Operation(summary = "약속 목록 조회",
		description = "인증된 사용자의 약속 목록을 슬라이스 단위로 조회합니다. 무한 스크롤 방식으로 구현됩니다.",
		security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "약속 목록 조회 성공",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 페이지 요청 (page 또는 size가 유효하지 않음)",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class)))
	})
	@GetMapping
	public BaseResponse<PromiseResponse.PromiseListResponse> getPromises(
		@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
		@Parameter(description = "페이지 번호 (0부터 시작)", required = true, example = "0")
		@RequestParam int page,
		@Parameter(description = "페이지당 데이터 수", required = true, example = "10")
		@RequestParam int size) {
		return BaseResponse.onSuccess(promiseQueryService.getPromises(userDetails.getMember().getId(), page, size));
	}

	@Operation(summary = "특정 약속 상세 조회",
		description = "인증된 사용자가 특정 약속의 상세 내용을 조회합니다.",
		security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "약속 상세 조회 성공",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "403", description = "해당 약속에 접근 권한 없음",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "404", description = "약속을 찾을 수 없음",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class)))
	})
	@GetMapping("/{promiseId}")
	public BaseResponse<PromiseResponse.PromiseDetailResponse> getPromiseDetail(
		@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
		@Parameter(description = "조회할 약속 ID", required = true, example = "1")
		@PathVariable Long promiseId) {
		return BaseResponse.onSuccess(promiseQueryService.getPromiseDetail(userDetails.getMember().getId(), promiseId));
	}

	@Operation(summary = "약속 생성",
		description = "인증된 사용자가 새로운 약속을 생성합니다.",
		security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "약속 생성 성공",
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
	public BaseResponse<String> createPromise(
		@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody @Valid PromiseRequest request) {
		promiseCommandService.createPromise(userDetails.getMember().getId(), request.getContent());
		return BaseResponse.onSuccess("약속 생성 성공");
	}

	@Operation(summary = "약속 전체 개수 조회",
		description = "인증된 사용자의 전체 약속 개수를 조회합니다.",
		security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "약속 개수 조회 성공",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class)))
	})
	@GetMapping("/count")
	public BaseResponse<Long> getPromiseCount(
		@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
		return BaseResponse.onSuccess(promiseQueryService.getPromiseCount(userDetails.getMember().getId()));
	}

	@Operation(summary = "약속 삭제",
		description = "인증된 사용자가 특정 약속을 삭제합니다.",
		security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "약속 삭제 성공",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "403", description = "삭제 권한 없음",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "404", description = "약속이 존재하지 않음",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class)))
	})
	@DeleteMapping("/{promiseId}")
	public BaseResponse<String> deletePromise(
		@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
		@Parameter(description = "삭제할 약속 ID", required = true, example = "1")
		@PathVariable Long promiseId) {
		promiseCommandService.deletePromise(userDetails.getMember().getId(), promiseId);
		return BaseResponse.onSuccess("약속 삭제 성공");
	}

	@Operation(summary = "특정 날짜 약속 조회",
		description = "인증된 사용자의 특정 날짜에 대한 약속 목록을 슬라이스 단위로 조회합니다.",
		security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "특정 날짜 약속 조회 성공",
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
	public BaseResponse<PromiseResponse.PromiseListResponse> getPromisesByDate(
		@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
		@Parameter(description = "조회할 날짜 (yyyy-MM-dd 형식)", required = true, example = "2025-02-13")
		@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
		@Parameter(description = "페이지 번호 (0부터 시작)", required = true, example = "0")
		@RequestParam int page,
		@Parameter(description = "페이지당 데이터 수", required = true, example = "10")
		@RequestParam int size) {
		return BaseResponse.onSuccess(
			promiseQueryService.getPromisesByDate(userDetails.getMember().getId(), date, page, size));
	}
}