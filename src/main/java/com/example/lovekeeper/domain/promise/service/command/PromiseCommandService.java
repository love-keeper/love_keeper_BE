package com.example.lovekeeper.domain.promise.service.command;

public interface PromiseCommandService {

	void createPromise(Long memberId, String content);

	void deletePromise(Long memberId, Long promiseId);
}
