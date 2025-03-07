package com.example.lovekeeper.domain.letter.dto.response;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Slice;

import com.example.lovekeeper.domain.letter.model.Letter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LetterResponse {

	@Builder
	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	public static class LetterListResponse {
		private List<LetterDetailResponse> letterList;  // 편지 목록
		private Boolean isFirst;                  // 첫 페이지 여부
		private Boolean isLast;                   // 마지막 페이지 여부
		private Boolean hasNext;                  // 다음 페이지가 있는지 여부

		public static LetterListResponse from(Slice<Letter> letterSlice) {
			return LetterListResponse.builder()
				.letterList(letterSlice.getContent().stream()
					.map(LetterDetailResponse::from)  // LetterDetailResponse의 from 메서드를 사용하여 변환
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
	public static class LetterDetailResponse {

		private String senderNickname;
		private String receiverNickname;
		private String content;
		private LocalDate sentDate;

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
