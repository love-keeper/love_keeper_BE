package com.example.lovekeeper.domain.couple.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Schema(description = "커플 연결 요청")
public class ConnectCoupleRequest {

	@Schema(description = "초대 코드", example = "abcd1234")
	@NotBlank(message = "초대 코드는 필수입니다.")
	private String inviteCode;

}
