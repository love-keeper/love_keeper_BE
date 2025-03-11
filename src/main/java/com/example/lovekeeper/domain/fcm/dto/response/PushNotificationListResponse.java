package com.example.lovekeeper.domain.fcm.dto.response;

import java.util.List;

import org.springframework.data.domain.Slice;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PushNotificationListResponse {
	private List<PushNotificationResponse> notifications; // 알림 목록
	private int page; // 현재 페이지 번호
	private int size; // 페이지당 데이터 수
	private boolean hasNext; // 다음 데이터가 있는지 여부
	private long totalElementsFetched; // 지금까지 가져온 총 데이터 수 (선택적, 프론트에서 상태 관리에 유용)

	public static PushNotificationListResponse fromSlice(
		Slice<PushNotificationResponse> slice,
		long totalElementsFetched
	) {
		return PushNotificationListResponse.builder()
			.notifications(slice.getContent())
			.page(slice.getNumber())
			.size(slice.getSize())
			.hasNext(slice.hasNext())
			.totalElementsFetched(totalElementsFetched)
			.build();
	}
}