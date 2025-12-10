package com.study.manca.discount;

import com.study.manca.entity.Member.MemberGrade;

import java.math.BigDecimal;

/**
 * 할인 정책 인터페이스
 *
 * 모든 할인 정책은 이 인터페이스를 구현해야 합니다.
 * Spring이 구현체들을 자동으로 찾아 List로 주입합니다.
 */
public interface DiscountPolicy {

    /**
     * 할인 적용된 가격 계산
     * @param price 원래 가격
     * @return 할인 적용된 가격
     */
    BigDecimal calculateDiscount(BigDecimal price);

    /**
     * 해당 등급을 지원하는지 확인
     * @param grade 회원 등급
     * @return 지원 여부
     */
    boolean supports(MemberGrade grade);
}
