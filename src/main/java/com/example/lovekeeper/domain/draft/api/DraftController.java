package com.example.lovekeeper.domain.draft.api;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.lovekeeper.domain.draft.dto.request.SaveDraftRequest;
import com.example.lovekeeper.domain.draft.dto.response.DraftResponse;
import com.example.lovekeeper.domain.draft.service.command.DraftCommandService;
import com.example.lovekeeper.domain.draft.service.query.DraftQueryService;
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

@Tag(name = "편지 임시 저장", description = "편지를 임시 저장하는 API")
@Slf4j
@RestController
@RequestMapping("/api/drafts")
@RequiredArgsConstructor
public class DraftController {

	private final DraftQueryService draftQueryService;
	private final DraftCommandService draftCommandService;

	@Operation(summary = "임시 저장된 편지 조회",
		description = "인증된 사용자의 특정 순서에 해당하는 임시 저장된 편지를 조회합니다.",
		security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "임시 저장된 편지 조회 성공",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 순서 (order가 범위를 벗어남)",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "404", description = "해당 순서에 임시 저장된 편지가 없음",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class)))
	})
	@GetMapping("/{order}")
	public BaseResponse<DraftResponse> getDraft(
		@Parameter(description = "조회할 임시 저장 편지의 순서 (1~4)", required = true, example = "1")
		@PathVariable int order,
		@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
		Long memberId = userDetails.getMember().getId();
		return BaseResponse.onSuccess(draftQueryService.getDraft(memberId, order));
	}

	@Operation(summary = "편지 임시 저장",
		description = "인증된 사용자가 편지를 특정 순서에 임시 저장합니다.",
		security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "편지가 임시 저장되었습니다.",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터 (순서 범위 초과, 내용 누락)",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "409", description = "해당 순서에 이미 임시 저장된 편지가 있음",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class)))
	})
	@PostMapping
	public BaseResponse<String> saveDraft(
		@RequestBody @Valid SaveDraftRequest request,
		@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
		Long memberId = userDetails.getMember().getId();
		draftCommandService.saveDraft(memberId, request);
		return BaseResponse.onSuccess("편지가 임시 저장되었습니다.");
	}

	@Operation(summary = "임시 저장된 편지 삭제",
		description = "인증된 사용자가 특정 순서에 해당하는 임시 저장된 편지를 삭제합니다.",
		security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "편지가 삭제되었습니다.",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 순서 (order가 범위를 벗어남)",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class))),
		@ApiResponse(responseCode = "404", description = "해당 순서에 임시 저장된 편지가 없음",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = BaseResponse.class)))
	})
	@DeleteMapping("/{order}")
	public BaseResponse<String> deleteDraft(
		@Parameter(description = "삭제할 임시 저장 편지의 순서 (1~4)", required = true, example = "1")
		@PathVariable int order,
		@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
		Long memberId = userDetails.getMember().getId();
		draftCommandService.deleteDraft(memberId, order);
		return BaseResponse.onSuccess("편지가 삭제되었습니다.");
	}
}