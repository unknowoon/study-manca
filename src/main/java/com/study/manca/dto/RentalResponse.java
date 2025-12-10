package com.study.manca.dto;

import com.study.manca.entity.Member.MemberGrade;
import com.study.manca.entity.Rental;
import com.study.manca.entity.Rental.RentalStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class RentalResponse {
    private Long id;
    private Long memberId;
    private String memberName;
    private MemberGrade memberGrade;
    private Long bookId;
    private String bookTitle;
    private String bookCode;
    private LocalDateTime rentalDateTime;
    private LocalDateTime dueDateTime;
    private LocalDateTime returnDateTime;
    private RentalStatus status;
    private BigDecimal rentalFee;
    private BigDecimal discountedFee;
    private String remarks;
    private LocalDateTime createdAt;

    public static RentalResponse from(Rental rental) {
        return RentalResponse.builder()
                .id(rental.getId())
                .memberId(rental.getMember().getId())
                .memberName(rental.getMember().getName())
                .memberGrade(rental.getMember().getGrade())
                .bookId(rental.getBook().getId())
                .bookTitle(rental.getBook().getTitle())
                .bookCode(rental.getBook().getBookCode())
                .rentalDateTime(rental.getRentalDateTime())
                .dueDateTime(rental.getDueDateTime())
                .returnDateTime(rental.getReturnDateTime())
                .status(rental.getStatus())
                .rentalFee(rental.getRentalFee())
                .discountedFee(rental.getDiscountedFee())
                .remarks(rental.getRemarks())
                .createdAt(rental.getCreatedAt())
                .build();
    }
}
