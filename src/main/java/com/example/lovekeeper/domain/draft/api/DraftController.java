package com.example.lovekeeper.domain.draft.api;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

	/**
	 * 편지 임시 저장 조회
	 * @param order
	 * @param userDetails
	 * @return
	 */
	@GetMapping("/{order}")
	public BaseResponse<DraftResponse> getDraft(
		@PathVariable int order,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		Long memberId = userDetails.getMember().getId();
		return BaseResponse.onSuccess(draftQueryService.getDraft(memberId, order));
	}

	/**
	 * 편지 임시 저장
	 * @param request
	 * @param userDetails
	 * @return
	 */
	@PostMapping
	public BaseResponse<String> saveDraft(
		@RequestBody @Valid SaveDraftRequest request,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {

		Long memberId = userDetails.getMember().getId();

		draftCommandService.saveDraft(memberId, request);
		return BaseResponse.onSuccess("편지가 임시 저장되었습니다.");
	}
}
