package com.example.lovekeeper.domain.promise.service.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.lovekeeper.domain.member.repository.MemberRepository;
import com.example.lovekeeper.domain.promise.repository.PromiseRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PromiseCommandServiceImpl implements PromiseCommandService {

	private final PromiseRepository promiseRepository;
	private final MemberRepository memberRepository;

	@Override
	public void createPromise(Long memberId, String content) {

		// 현재 멤버 가져오기
		
	}
}
