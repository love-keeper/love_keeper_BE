package com.example.lovekeeper.domain.member.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.lovekeeper.domain.member.model.Member;
import com.example.lovekeeper.domain.member.model.MemberStatus;
import com.example.lovekeeper.domain.member.model.Provider;

public interface MemberRepository extends JpaRepository<Member, Long> {

	boolean existsByEmail(String email);

	Optional<Member> findByEmail(String email);

	Optional<Member> findByProviderAndProviderId(Provider provider, String providerId);

	Optional<Member> findByInviteCode(String inviteCode);

	// 삭제 대상 회원 조회를 위한 메서드 추가
	List<Member> findByMemberStatusAndUpdatedAt(MemberStatus status, LocalDateTime dateTime);

}
