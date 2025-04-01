package com.example.lovekeeper.global.scheduler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.lovekeeper.domain.couple.model.Couple;
import com.example.lovekeeper.domain.couple.model.CoupleStatus;
import com.example.lovekeeper.domain.couple.repository.CoupleRepository;
import com.example.lovekeeper.domain.member.model.Member;
import com.example.lovekeeper.domain.member.model.MemberStatus;
import com.example.lovekeeper.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CleanupScheduler {

	private static final int DELETE_DAYS = 15;  // 90일 후 삭제
	private final MemberRepository memberRepository;
	private final CoupleRepository coupleRepository;

	/**
	 * 매일 자정에 실행
	 * - 탈퇴한지 90일이 지난 회원 삭제
	 * - 연결이 끊긴지 90일이 지난 커플 삭제
	 */
	@Scheduled(cron = "0 0 0 * * *")
	@Transactional
	public void cleanup() {
		cleanupMembers();
		cleanupCouples();
	}

	private void cleanupMembers() {
		LocalDateTime deleteDate = LocalDateTime.now().minusDays(DELETE_DAYS);
		List<Member> membersToDelete = memberRepository.findByStatusAndDeletedAtBefore(
			MemberStatus.DELETED,
			deleteDate
		);

		log.info("Deleting {} members that were marked as deleted before {}",
			membersToDelete.size(), deleteDate);

		for (Member member : membersToDelete) {
			log.info("Deleting member: id={}, email={}", member.getId(), member.getEmail());
			memberRepository.delete(member);
		}
	}

	private void cleanupCouples() {
		LocalDate deleteDate = LocalDate.now().minusDays(DELETE_DAYS);
		List<Couple> couplesToDelete = coupleRepository.findByStatusAndEndedAtBefore(
			CoupleStatus.DISCONNECTED,
			deleteDate
		);

		log.info("Deleting {} couples that were disconnected before {}",
			couplesToDelete.size(), deleteDate);

		for (Couple couple : couplesToDelete) {
			log.info("Deleting couple: id={}, member1={}, member2={}",
				couple.getId(),
				couple.getMember1().getId(),
				couple.getMember2().getId());
			coupleRepository.delete(couple);
		}
	}
}