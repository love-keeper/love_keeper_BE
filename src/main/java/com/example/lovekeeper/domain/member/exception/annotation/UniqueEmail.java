package com.example.lovekeeper.domain.member.exception.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.example.lovekeeper.global.validator.UniqueEmailValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * 이메일 중복 검증을 위한 커스텀 어노테이션
 */
@Documented
@Constraint(validatedBy = UniqueEmailValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})  // @RequestParam에 직접 붙이거나, DTO의 필드에 붙일 때 사용
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueEmail {

	String message() default "이미 사용 중인 이메일입니다.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}

