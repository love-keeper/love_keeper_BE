// AuthController.java
package com.example.lovekeeper.domain.auth.web;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.lovekeeper.domain.auth.dto.request.SignUpRequest;
import com.example.lovekeeper.domain.auth.dto.response.SignUpResponse;
import com.example.lovekeeper.domain.auth.service.command.AuthCommandService;
import com.example.lovekeeper.domain.member.model.Provider;
import com.example.lovekeeper.global.common.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "인증", description = "회원가입, 로그인, 로그아웃 등 인증 관련 API")
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

	private final AuthCommandService authCommandService;

	@Operation(summary = "회원가입", description = "새로운 회원을 등록합니다.")
	@ApiResponse(responseCode = "201", description = "회원가입 성공")
	@PostMapping(value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public BaseResponse<SignUpResponse> signUp(
		@RequestParam @Email String email,
		@RequestParam(required = false) @Pattern(
			regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,20}$",
			message = "비밀번호는 8~20자의 영문, 숫자, 특수문자를 포함해야 합니다.")
		String password, // 비밀번호는 필수가 아님
		@RequestParam @Size(min = 2, max = 10) String nickname,
		@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate birthDate,
		@RequestParam(required = false) MultipartFile profileImage,
		@RequestParam Provider provider, // 필수임.
		@RequestParam(required = false) String providerId // (Local 회원가입 시 필수가 아님)
	) {

		return BaseResponse.onSuccessCreate(authCommandService.signUpMember(
			SignUpRequest.of(email, password, nickname, birthDate, profileImage, provider, providerId)));
	}
}
