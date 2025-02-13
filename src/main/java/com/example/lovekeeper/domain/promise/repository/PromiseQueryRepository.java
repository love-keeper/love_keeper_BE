package com.example.lovekeeper.domain.promise.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.lovekeeper.domain.calendar.dto.response.DateCountResponse;
import com.example.lovekeeper.domain.promise.model.QPromise;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PromiseQueryRepository {

	private final JPAQueryFactory queryFactory;

	public List<DateCountResponse> findPromiseCountByCoupleAndYearMonth(Long coupleId, int year, int month) {
		QPromise promise = QPromise.promise;

		return queryFactory
			.select(Projections.constructor(DateCountResponse.class,
				promise.promisedAt,
				promise.count()
			))
			.from(promise)
			.where(promise.couple.id.eq(coupleId)
				.and(promise.promisedAt.year().eq(year))
				.and(promise.promisedAt.month().eq(month)))
			.groupBy(promise.promisedAt)
			.fetch();
	}
}
