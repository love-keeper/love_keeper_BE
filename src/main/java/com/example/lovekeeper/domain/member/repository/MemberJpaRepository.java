package com.example.lovekeeper.domain.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.lovekeeper.domain.member.model.Member;
import com.example.lovekeeper.domain.member.model.Provider;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {

	boolean existsByEmail(String email);

	Optional<Member> findByEmail(String email);

	Optional<Member> findByProviderAndProviderId(Provider provider, String providerId);

}
