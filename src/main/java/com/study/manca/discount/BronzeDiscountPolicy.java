package com.study.manca.discount;

import com.study.manca.entity.Member.MemberGrade;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * BRONZE 등급 할인 정책
 * 할인율: 0% (할인 없음)
 */
@Component
public class BronzeDiscountPolicy implements DiscountPolicy {

    @Override
    public BigDecimal calculateDiscount(BigDecimal price) {
        return price;  // 할인 없음
    }

    @Override
    public boolean supports(MemberGrade grade) {
        return grade == MemberGrade.BRONZE;
    }
}
