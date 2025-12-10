package com.study.manca.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RentalRequest {
    private Long memberId;
    private Long bookId;
    private Integer rentalDays;  // 대여 기간 (기본 7일)
    private String remarks;
}
