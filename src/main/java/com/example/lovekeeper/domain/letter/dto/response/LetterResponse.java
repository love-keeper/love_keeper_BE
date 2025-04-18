package com.example.lovekeeper.domain.letter.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Slice;

import com.example.lovekeeper.domain.letter.model.Letter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(description = "편지 관련 응답 DTO")
public class LetterResponse {

	@Builder
	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	@Schema(description = "편지 목록 응답 DTO (Slice 기반)")
	public static class LetterListResponse {
		@Schema(description = "편지 상세 정보 목록", required = true)
		private List<LetterDetailResponse> letterList;

		@Schema(description = "첫 페이지 여부", example = "true", required = true)
		private Boolean isFirst;

		@Schema(description = "마지막 페이지 여부", example = "false", required = true)
		private Boolean isLast;

		@Schema(description = "다음 페이지가 있는지 여부", example = "true", required = true)
		private Boolean hasNext;

		public static LetterListResponse from(Slice<Letter> letterSlice) {
			return LetterListResponse.builder()
				.letterList(letterSlice.getContent().stream()
					.map(LetterDetailResponse::from)
					.collect(Collectors.toList()))
				.isFirst(letterSlice.isFirst())
				.isLast(letterSlice.isLast())
				.hasNext(letterSlice.hasNext())
				.build();
		}
	}

	@Builder
	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	@Schema(description = "개별 편지 상세 응답 DTO")
	public static class LetterDetailResponse {
		@Schema(description = "보낸 사람 닉네임", example = "lover", required = true)
		private String senderNickname;

		@Schema(description = "받는 사람 닉네임", example = "honey", required = true)
		private String receiverNickname;

		@Schema(description = "편지 내용", example = "사랑해요!", required = true)
		private String content;

		@Schema(description = "보낸 날짜 (yyyy-MM-dd)", example = "2025-02-13", required = true)
		private LocalDateTime sentDate;

		public static LetterDetailResponse from(Letter letter) {
			return LetterDetailResponse.builder()
				.senderNickname(letter.getSender().getNickname())
				.receiverNickname(letter.getReceiver().getNickname())
				.content(letter.getContent())
				.sentDate(letter.getSentDate())
				.build();
		}
	}
}