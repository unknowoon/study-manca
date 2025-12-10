package com.study.manca.dto;

import com.study.manca.entity.Member.MemberGrade;
import com.study.manca.entity.Order;
import com.study.manca.entity.Order.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class OrderResponse {
    private Long id;
    private Long memberId;
    private String memberName;
    private MemberGrade memberGrade;
    private String seatNumber;
    private String menuName;
    private Integer quantity;
    private BigDecimal totalPrice;
    private BigDecimal discountedPrice;
    private LocalDateTime orderDateTime;
    private OrderStatus status;
    private String remarks;
    private LocalDateTime createdAt;

    public static OrderResponse from(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .memberId(order.getMember().getId())
                .memberName(order.getMember().getName())
                .memberGrade(order.getMember().getGrade())
                .seatNumber(order.getSeat().getSeatNumber())
                .menuName(order.getMenu().getName())
                .quantity(order.getQuantity())
                .totalPrice(order.getTotalPrice())
                .discountedPrice(order.getDiscountedPrice())
                .orderDateTime(order.getOrderDateTime())
                .status(order.getStatus())
                .remarks(order.getRemarks())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
