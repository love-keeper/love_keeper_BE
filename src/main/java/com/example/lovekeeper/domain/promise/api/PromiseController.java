package com.example.lovekeeper.domain.promise.api;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.lovekeeper.domain.promise.dto.request.PromiseRequest;
import com.example.lovekeeper.domain.promise.dto.response.PromiseResponse;
import com.example.lovekeeper.domain.promise.service.command.PromiseCommandService;
import com.example.lovekeeper.domain.promise.service.query.PromiseQueryService;
import com.example.lovekeeper.global.common.BaseResponse;
import com.example.lovekeeper.global.security.user.CustomUserDetails;

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

	/**
	 * 약속 읽기
	 */
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public BaseResponse<PromiseResponse.PromiseListResponse> getPromises(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam int page,
		@RequestParam int size
	) {

		return BaseResponse.onSuccess(promiseQueryService.getPromises(userDetails.getMember().getId(), page, size));
	}

	/**
	 * 약속 생성
	 */
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public BaseResponse<String> createPromise(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody @Valid PromiseRequest request
	) {

		promiseCommandService.createPromise(userDetails.getMember().getId(), request.getContent());

		return BaseResponse.onSuccess("약속 생성 성공");
	}

	/**
	 * 약속 삭제
	 */
	@DeleteMapping("/{promiseId}")
	@ResponseStatus(HttpStatus.OK)
	public BaseResponse<String> deletePromise(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable Long promiseId
	) {

		promiseCommandService.deletePromise(userDetails.getMember().getId(), promiseId);

		return BaseResponse.onSuccess("약속 삭제 성공");
	}
}
