// AuthController.java
package com.example.lovekeeper.domain.auth.api;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.lovekeeper.domain.auth.dto.request.ChangeBirthdayRequest;
import com.example.lovekeeper.domain.auth.dto.request.ChangeNicknameRequest;
import com.example.lovekeeper.domain.auth.dto.request.ChangePasswordRequest;
import com.example.lovekeeper.domain.auth.dto.request.EmailDuplicationRequest;
import com.example.lovekeeper.domain.auth.dto.request.ResetPasswordRequest;
import com.example.lovekeeper.domain.auth.dto.request.SignUpRequest;
import com.example.lovekeeper.domain.auth.dto.response.ChangeBirthdayResponse;
import com.example.lovekeeper.domain.auth.dto.response.ChangeNicknameResponse;
import com.example.lovekeeper.domain.auth.dto.response.ReissueResponse;
import com.example.lovekeeper.domain.auth.dto.response.SignUpResponse;
import com.example.lovekeeper.domain.auth.service.command.AuthCommandService;
import com.example.lovekeeper.domain.auth.service.command.EmailAuthCommandService;
import com.example.lovekeeper.domain.auth.service.query.AuthQueryService;
import com.example.lovekeeper.domain.member.exception.annotation.UniqueEmail;
import com.example.lovekeeper.domain.member.model.Provider;
import com.example.lovekeeper.global.common.BaseResponse;
import com.example.lovekeeper.global.security.user.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
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
	private final AuthQueryService authQueryService;

	private final EmailAuthCommandService emailauthcommandservice;

	/**
	 * 이메일 중복 확인
	 */
	@Operation(summary = "회원가입이 가능한지 확인", description = "이메일 중복 확인")
	@ApiResponse(responseCode = "200", description = "이메일 중복 확인 성공")
	@GetMapping("/email-duplication")
	public BaseResponse<String> checkEmailDuplication(
		@RequestBody @Valid EmailDuplicationRequest emailDuplicationRequest) {
		authQueryService.checkEmailDuplication(emailDuplicationRequest.getEmail());
		return BaseResponse.onSuccess("사용 가능한 이메일입니다.");
	}

	/**
	 * 회원가입
	 */
	@Operation(summary = "회원가입", description = "새로운 회원을 등록합니다.")
	@ApiResponse(responseCode = "201", description = "회원가입 성공")
	@PostMapping(value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public BaseResponse<SignUpResponse> signUp(
		@RequestParam @Email @UniqueEmail String email,
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

	/**
	 * 닉네임 변경
	 */
	@PatchMapping("/nickname")
	public BaseResponse<ChangeNicknameResponse> changeNickname(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody @Valid ChangeNicknameRequest changeNicknameRequest) {

		return BaseResponse.onSuccess(authCommandService.changeNickname(userDetails.getMember().getId(),
			changeNicknameRequest.getNickname()));
	}

	/**
	 * 생일 변경
	 */
	@PatchMapping("/birthday")
	public BaseResponse<ChangeBirthdayResponse> changeBirthday(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody @Valid ChangeBirthdayRequest changeBirthdayRequest) {

		return BaseResponse.onSuccess(authCommandService.changeBirthday(userDetails.getMember().getId(),
			changeBirthdayRequest.getBirthday()));
	}

	/**
	 * 비밀번호 변경
	 */
	@PatchMapping("/password")
	public BaseResponse<String> changePassword(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody @Valid ChangePasswordRequest request
	) {

		authCommandService.changePassword(userDetails.getMember().getId(), request);

		return BaseResponse.onSuccess("비밀번호 변경 성공");
	}

	/**
	 * 비밀번호 변경 요청
	 */
	@Operation(summary = "비밀번호 변경 요청", description = "사용자의 이메일로 비밀번호 변경 링크를 보냅니다.")
	@PostMapping("/password/reset-request")
	public BaseResponse<String> resetPasswordRequest(@RequestBody @Valid ResetPasswordRequest request) {
		// 이메일 인증 코드 생성 및 발송
		emailauthcommandservice.sendPasswordChangeLink(request.getEmail());
		return BaseResponse.onSuccess("비밀번호 변경 링크가 이메일로 발송되었습니다.");
	}

	/**
	 * 토큰 재발급
	 */
	@Operation(summary = "토큰 재발급", description = "쿠키에 담긴 Refresh Token을 이용해 새 Access/Refresh Token을 발급받습니다.")
	@ApiResponse(responseCode = "200", description = "토큰 재발급 성공")
	@PostMapping("/reissue")
	public BaseResponse<ReissueResponse> reissueRefreshToken(HttpServletRequest request, HttpServletResponse response) {

		// 1) 쿠키에서 기존 Refresh Token 추출
		String oldRefreshToken = extractRefreshTokenFromCookie(request);
		if (oldRefreshToken == null) {
			return BaseResponse.onFailure("AUTH003", "Refresh Token이 쿠키에 존재하지 않습니다.", null);
		}

		// 2) Service에서 재발급 로직 처리
		ReissueResponse reissueResponse = authCommandService.reissueRefreshToken(oldRefreshToken);

		// 3) 새 Access Token을 헤더에 담는다
		response.setHeader("Authorization", "Bearer " + reissueResponse.getAccessToken());

		// 4) 새 Refresh Token을 쿠키에 담아 내려준다
		Cookie refreshCookie = new Cookie("refresh_token", reissueResponse.getRefreshToken());
		refreshCookie.setHttpOnly(true);
		refreshCookie.setPath("/");
		refreshCookie.setMaxAge(7 * 24 * 60 * 60); // 7일, (초 단위)

		// 필요 시 HTTPS 환경에서만 사용하도록 secure 옵션 활성화
		// refreshCookie.setSecure(true);

		response.addCookie(refreshCookie);

		log.info("[AuthController] 토큰 재발급 - Access/Refresh Token 생성 완료");

		// 클라이언트로 ReissueResponse 객체(AccessToken, RefreshToken)를 JSON으로도 보내고자 하면,
		// Refresh Token은 굳이 바디로 내려주지 않고, AccessToken만 제공해도 됩니다(정책에 따라).
		return BaseResponse.onSuccessCreate(reissueResponse);
	}

	/**
	 * 쿠키에서 Refresh Token 추출
	 */
	private String extractRefreshTokenFromCookie(HttpServletRequest request) {
		if (request.getCookies() == null) {
			return null;
		}
		for (Cookie cookie : request.getCookies()) {
			if ("refresh_token".equals(cookie.getName())) {
				return cookie.getValue();
			}
		}
		return null;
	}

}
