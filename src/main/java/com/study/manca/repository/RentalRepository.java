package com.study.manca.repository;

import com.study.manca.entity.Rental;
import com.study.manca.entity.Rental.RentalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RentalRepository extends JpaRepository<Rental, Long> {
    List<Rental> findByMemberId(Long memberId);
    List<Rental> findByStatus(RentalStatus status);
    List<Rental> findByMemberIdAndStatus(Long memberId, RentalStatus status);
    int countByMemberIdAndStatus(Long memberId, RentalStatus status);
}
