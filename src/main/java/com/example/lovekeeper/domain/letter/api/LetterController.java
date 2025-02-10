package com.example.lovekeeper.domain.letter.api;

import java.time.Month;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.lovekeeper.domain.letter.dto.request.SendLetterRequest;
import com.example.lovekeeper.domain.letter.dto.response.LetterCountByDateResponse;
import com.example.lovekeeper.domain.letter.dto.response.LetterResponse;
import com.example.lovekeeper.domain.letter.service.command.LetterCommandService;
import com.example.lovekeeper.domain.letter.service.query.LetterQueryService;
import com.example.lovekeeper.global.common.BaseResponse;
import com.example.lovekeeper.global.security.user.CustomUserDetails;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "편지", description = "편지 API")
@Slf4j
@RestController
@RequestMapping("/api/letters")
@RequiredArgsConstructor
public class LetterController {

	private final LetterQueryService letterQueryService;
	private final LetterCommandService letterCommandService;

	/**
	 * 편지 목록 조회 API (무한 스크롤)
	 */
	@GetMapping("/list")
	public BaseResponse<LetterResponse.LetterListResponse> getLetters(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam int page, @RequestParam int size
	) {

		return BaseResponse.onSuccess(letterQueryService.getLetters(userDetails.getMember().getId(), page,
			size));
	}

	// 연도 + 월을 기준으로 각 날짜별 편지 수 반환
	@GetMapping("/monthly-letter-count")
	public BaseResponse<List<LetterCountByDateResponse>> getLetterCountByMonth(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam int year,
		@RequestParam int month // 1 for January, 2 for February, etc.
	) {

		Month selectedMonth = Month.of(month);
		List<LetterCountByDateResponse> letterCountByDateResponses = letterQueryService.getLetterCountByMonth(year,
			selectedMonth, userDetails.getMember().getId());

		return BaseResponse.onSuccess(letterCountByDateResponses);
	}

	/**
	 * 편지 보내기 API
	 */
	@PostMapping
	public BaseResponse<String> sendLetter(
		@RequestBody SendLetterRequest sendLetterRequest,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		log.info("편지를 보냅니다.");

		letterCommandService.sendLetter(userDetails.getMember().getId(), sendLetterRequest.getContent());

		return BaseResponse.onSuccess("편지를 보냅니다.");

	}
}
