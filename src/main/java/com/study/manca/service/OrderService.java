package com.study.manca.service;

import com.study.manca.discount.DiscountPolicy;
import com.study.manca.dto.OrderRequest;
import com.study.manca.dto.OrderResponse;
import com.study.manca.entity.Member;
import com.study.manca.entity.Menu;
import com.study.manca.entity.Order;
import com.study.manca.entity.Seat;
import com.study.manca.repository.MemberRepository;
import com.study.manca.repository.MenuRepository;
import com.study.manca.repository.OrderRepository;
import com.study.manca.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final MenuRepository menuRepository;
    private final SeatRepository seatRepository;

    /**
     * 핵심: 모든 할인 정책이 List로 주입됨 (다형성)
     * Spring이 DiscountPolicy를 구현한 모든 Bean을 찾아서 주입
     */
    private final List<DiscountPolicy> discountPolicies;

    public List<OrderResponse> findAll() {
        return orderRepository.findAll().stream()
                .map(OrderResponse::from)
                .collect(Collectors.toList());
    }

    public OrderResponse findById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + id));
        return OrderResponse.from(order);
    }

    public List<OrderResponse> findByMemberId(Long memberId) {
        return orderRepository.findByMemberId(memberId).stream()
                .map(OrderResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderResponse create(OrderRequest request) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        Menu menu = menuRepository.findById(request.getMenuId())
                .orElseThrow(() -> new IllegalArgumentException("Menu not found"));

        Seat seat = seatRepository.findById(request.getSeatId())
                .orElseThrow(() -> new IllegalArgumentException("Seat not found"));

        // 총액 계산
        BigDecimal totalPrice = menu.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));

        // 회원 등급에 맞는 할인 정책 적용 (다형성 활용)
        BigDecimal discountedPrice = applyDiscount(member, totalPrice);

        Order order = Order.builder()
                .member(member)
                .menu(menu)
                .seat(seat)
                .quantity(request.getQuantity())
                .totalPrice(totalPrice)
                .discountedPrice(discountedPrice)
                .orderDateTime(LocalDateTime.now())
                .remarks(request.getRemarks())
                .build();

        Order savedOrder = orderRepository.save(order);
        return OrderResponse.from(savedOrder);
    }

    /**
     * 회원 등급에 맞는 할인 정책을 찾아 적용
     *
     * 다형성의 핵심:
     * - discountPolicies에는 4개의 구현체가 들어있음
     * - supports()로 해당 등급을 지원하는 정책을 찾음
     * - 찾은 정책의 calculateDiscount()를 호출
     */
    private BigDecimal applyDiscount(Member member, BigDecimal totalPrice) {
        return discountPolicies.stream()
                .filter(policy -> policy.supports(member.getGrade()))
                .findFirst()
                .map(policy -> policy.calculateDiscount(totalPrice))
                .orElse(totalPrice);
    }

    @Transactional
    public OrderResponse updateStatus(Long id, Order.OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + id));

        order.setStatus(status);
        return OrderResponse.from(order);
    }

    @Transactional
    public void delete(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new IllegalArgumentException("Order not found with id: " + id);
        }
        orderRepository.deleteById(id);
    }
}
