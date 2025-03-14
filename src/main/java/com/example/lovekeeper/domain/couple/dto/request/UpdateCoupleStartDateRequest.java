package com.example.lovekeeper.domain.couple.dto.request;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Schema(description = "커플 시작일 변경 요청")
public class UpdateCoupleStartDateRequest {

	@Schema(description = "변경할 커플 시작일", example = "2021-01-01")
	@NotNull(message = "새로운 시작일은 필수입니다.")
	private LocalDate newStartDate;

}
