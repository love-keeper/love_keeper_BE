package com.example.lovekeeper.domain.promise.dto.response;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Slice;

import com.example.lovekeeper.domain.promise.model.Promise;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(description = "약속 관련 응답 DTO")
public class PromiseResponse {

	@Getter
	@Builder
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	@Schema(description = "약속 목록 응답 DTO (Slice 기반)")
	public static class PromiseListResponse {

		@Schema(description = "약속 상세 정보 목록", required = true)
		private List<PromiseDetailResponse> promiseList;

		@Schema(description = "첫 페이지 여부", example = "true", required = true)
		private Boolean isFirst;

		@Schema(description = "마지막 페이지 여부", example = "false", required = true)
		private Boolean isLast;

		@Schema(description = "다음 페이지가 있는지 여부", example = "true", required = true)
		private Boolean hasNext;

		public static PromiseListResponse from(Slice<Promise> promiseSlice) {
			return PromiseListResponse.builder()
				.promiseList(promiseSlice.getContent().stream()
					.map(PromiseDetailResponse::from)
					.collect(Collectors.toList()))
				.isFirst(promiseSlice.isFirst())
				.isLast(promiseSlice.isLast())
				.hasNext(promiseSlice.hasNext())
				.build();
		}
	}

	@Getter
	@Builder
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	@Schema(description = "개별 약속 상세 응답 DTO")
	public static class PromiseDetailResponse {

		@Schema(description = "생성한 멤버 ID", example = "1", required = true)
		private Long memberId;

		@Schema(description = "약속 ID", example = "1", required = true)
		private Long promiseId;

		@Schema(description = "약속 내용", example = "매일 10시에 전화하기", required = true)
		private String content;

		@Schema(description = "약속 날짜 (yyyy-MM-dd)", example = "2025-02-13", required = true)
		private LocalDate promisedAt;

		public static PromiseDetailResponse from(Promise promise) {
			return PromiseDetailResponse.builder()
				.memberId(promise.getMember().getId())
				.promiseId(promise.getId())
				.content(promise.getContent())
				.promisedAt(promise.getPromisedAt())
				.build();
		}
	}
}