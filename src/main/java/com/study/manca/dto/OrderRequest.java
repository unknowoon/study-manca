package com.study.manca.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderRequest {
    private Long memberId;
    private Long seatId;
    private Long menuId;
    private Integer quantity;
    private String remarks;
}
