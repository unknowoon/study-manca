package com.study.manca.repository;

import com.study.manca.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // 이메일로 사용자 조회
    Optional<Member> findByEmail(String email);

    // 이메일 중복 확인
    boolean existsByEmail(String email);
}
