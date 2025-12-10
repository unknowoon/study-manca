package com.study.manca.discount;

import com.study.manca.entity.Member.MemberGrade;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * GOLD 등급 할인 정책
 * 할인율: 10%
 */
@Component
public class GoldDiscountPolicy implements DiscountPolicy {

    private static final BigDecimal DISCOUNT_RATE = new BigDecimal("0.90");

    @Override
    public BigDecimal calculateDiscount(BigDecimal price) {
        return price.multiply(DISCOUNT_RATE);
    }

    @Override
    public boolean supports(MemberGrade grade) {
        return grade == MemberGrade.GOLD;
    }
}
