package com.example.lovekeeper.domain.promise.api;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.lovekeeper.domain.promise.dto.request.PromiseRequest;
import com.example.lovekeeper.domain.promise.service.command.PromiseCommandService;
import com.example.lovekeeper.global.common.BaseResponse;
import com.example.lovekeeper.global.security.user.CustomUserDetails;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "약속", description = "약속 API")
@Slf4j
@RestController
@RequestMapping("/api/promises")
@RequiredArgsConstructor
public class PromiseController {

	private final PromiseCommandService promiseCommandService;

	/**
	 * 약속 생성
	 */
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public BaseResponse<String> createPromise(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		PromiseRequest request
	) {

		promiseCommandService.createPromise(userDetails.getMember().getId(), request.getContent());

		return BaseResponse.onSuccess("약속 생성 성공");
	}
}
