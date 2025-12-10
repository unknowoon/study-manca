package com.study.manca.repository;

import com.study.manca.entity.Order;
import com.study.manca.entity.Order.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByMemberId(Long memberId);
    List<Order> findByStatus(OrderStatus status);
    List<Order> findBySeatId(Long seatId);
}
