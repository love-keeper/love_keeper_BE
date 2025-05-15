package com.example.lovekeeper.global.validator;

import org.springframework.stereotype.Component;

import com.example.lovekeeper.domain.member.exception.MemberErrorStatus;
import com.example.lovekeeper.domain.member.exception.annotation.UniqueEmail;
import com.example.lovekeeper.domain.member.repository.MemberRepository;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

	private final MemberRepository memberRepository;

	@Override
	public boolean isValid(String email, ConstraintValidatorContext context) {
		if (email == null || email.isBlank()) {
			return true;  // null/blank는 다른 Validator가 처리
		}

		boolean exists = memberRepository.existsByEmail(email);
		if (exists) {
			// Bean Validation 표준 방식 사용
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(MemberErrorStatus.DUPLICATE_EMAIL.getMessage())
				.addConstraintViolation();
			// false를 반환하면 Hibernate Validator가 ConstraintViolation 생성
			return false;
		}

		return true;
	}
}
