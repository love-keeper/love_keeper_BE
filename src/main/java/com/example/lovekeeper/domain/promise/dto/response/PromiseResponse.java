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
public class PromiseResponse {

	@Getter
	@Builder
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	public static class PromiseListResponse {

		private List<PromiseDetailResponse> promiseList;  // 약속 목록
		private Boolean isFirst;                  // 첫 페이지 여부
		private Boolean isLast;                   // 마지막 페이지 여부
		private Boolean hasNext;                  // 다음 페이지가 있는지 여부

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
	public static class PromiseDetailResponse {

		@Schema(description = "생성한 멤버 ID")
		private Long memberId;

		@Schema(description = "약속 ID")
		private Long promiseId;

		@Schema(description = "약속 내용")
		private String content;

		@Schema(description = "약속 날짜")
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
