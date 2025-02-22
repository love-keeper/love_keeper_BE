package com.example.lovekeeper.domain.auth.dto.request;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Getter;

@Getter
@Schema(description = "생일 변경 요청")
public class ChangeBirthdayRequest {

	@Schema(description = "변경할 생일", example = "1999-01-01")
	@Past(message = "과거의 날짜만 입력 가능합니다.")
	@NotNull(message = "생일을 입력해주세요.")
	private LocalDate birthday;

}
