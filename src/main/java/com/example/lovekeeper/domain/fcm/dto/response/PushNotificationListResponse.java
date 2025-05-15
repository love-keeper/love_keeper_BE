package com.example.lovekeeper.domain.fcm.dto.response;

import java.util.List;

import org.springframework.data.domain.Slice;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Schema(description = "푸시 알림 목록 응답")
public class PushNotificationListResponse {

	@Schema(description = "알림 목록", required = true)
	private List<PushNotificationResponse> notifications;

	@Schema(description = "현재 페이지 번호 (0부터 시작)", example = "0", required = true)
	private int page;

	@Schema(description = "페이지당 데이터 수", example = "10", required = true)
	private int size;

	@Schema(description = "다음 데이터가 있는지 여부", example = "true", required = true)
	private boolean hasNext;

	@Schema(description = "지금까지 가져온 총 데이터 수 (상태 관리용, 선택적)", example = "10", required = false)
	private long totalElementsFetched;

	public static PushNotificationListResponse fromSlice(
		Slice<PushNotificationResponse> slice,
		long totalElementsFetched) {
		return PushNotificationListResponse.builder()
			.notifications(slice.getContent())
			.page(slice.getNumber())
			.size(slice.getSize())
			.hasNext(slice.hasNext())
			.totalElementsFetched(totalElementsFetched)
			.build();
	}
}