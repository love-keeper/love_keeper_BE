package com.example.lovekeeper.domain.letter.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.lovekeeper.domain.calendar.dto.response.DateCountResponse;
import com.example.lovekeeper.domain.letter.model.QLetter;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class LetterQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;

	public List<DateCountResponse> findLetterCountByCoupleAndYearMonth(Long coupleId, int year, int month) {
		QLetter letter = QLetter.letter;

		return jpaQueryFactory
			.select(Projections.constructor(DateCountResponse.class,
				letter.sentDate,
				letter.count()))
			.from(letter)
			.where(letter.couple.id.eq(coupleId)
				.and(letter.sentDate.year().eq(year))
				.and(letter.sentDate.month().eq(month)))
			.groupBy(letter.sentDate)
			.fetch();
	}

	public List<DateCountResponse> findLetterCountByCoupleAndSpecificDate(Long coupleId, int year, int month, int day) {
		QLetter letter = QLetter.letter;

		return jpaQueryFactory
			.select(Projections.constructor(DateCountResponse.class,
				letter.sentDate,
				letter.count()))
			.from(letter)
			.where(letter.couple.id.eq(coupleId)
				.and(letter.sentDate.year().eq(year))
				.and(letter.sentDate.month().eq(month))
				.and(letter.sentDate.dayOfMonth().eq(day)))
			.groupBy(letter.sentDate)
			.fetch();
	}

	public long findTotalLetterCountByCoupleAndYearMonth(Long coupleId, int year, int month) {
		QLetter letter = QLetter.letter;

		Long count = jpaQueryFactory
			.select(letter.count())
			.from(letter)
			.where(letter.couple.id.eq(coupleId)
				.and(letter.sentDate.year().eq(year))
				.and(letter.sentDate.month().eq(month)))
			.fetchOne();

		return count != null ? count : 0;
	}

	public long findTotalLetterCountByCoupleAndSpecificDate(Long coupleId, int year, int month, int day) {
		QLetter letter = QLetter.letter;

		Long count = jpaQueryFactory
			.select(letter.count())
			.from(letter)
			.where(letter.couple.id.eq(coupleId)
				.and(letter.sentDate.year().eq(year))
				.and(letter.sentDate.month().eq(month))
				.and(letter.sentDate.dayOfMonth().eq(day)))
			.fetchOne();

		return count != null ? count : 0;
	}
}