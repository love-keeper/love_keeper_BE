package com.example.lovekeeper.domain.member.api;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.lovekeeper.domain.member.service.command.MemberCommandService;
import com.example.lovekeeper.global.common.BaseResponse;
import com.example.lovekeeper.global.security.user.CustomUserDetails;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "회원", description = "회원 관리 API")
@Slf4j
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

	private final MemberCommandService memberCommandService;

	@DeleteMapping
	public BaseResponse<String> withdrawMember(
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		memberCommandService.withdrawMember(userDetails.getMember().getId());
		return BaseResponse.onSuccess("회원 탈퇴가 완료되었습니다.");
	}

}
