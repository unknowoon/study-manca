package com.study.manca.service;

import com.study.manca.discount.DiscountPolicy;
import com.study.manca.dto.RentalRequest;
import com.study.manca.dto.RentalResponse;
import com.study.manca.entity.Book;
import com.study.manca.entity.Member;
import com.study.manca.entity.Rental;
import com.study.manca.entity.Rental.RentalStatus;
import com.study.manca.repository.BookRepository;
import com.study.manca.repository.MemberRepository;
import com.study.manca.repository.RentalRepository;
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
public class RentalService {

    private final RentalRepository rentalRepository;
    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;

    /**
     * 핵심: OrderService와 동일한 할인 정책을 재사용 (다형성)
     */
    private final List<DiscountPolicy> discountPolicies;

    private static final int DEFAULT_RENTAL_DAYS = 7;
    private static final int MAX_RENTAL_COUNT = 3;

    public List<RentalResponse> findAll() {
        return rentalRepository.findAll().stream()
                .map(RentalResponse::from)
                .collect(Collectors.toList());
    }

    public RentalResponse findById(Long id) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rental not found with id: " + id));
        return RentalResponse.from(rental);
    }

    public List<RentalResponse> findByMemberId(Long memberId) {
        return rentalRepository.findByMemberId(memberId).stream()
                .map(RentalResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public RentalResponse create(RentalRequest request) {
        // 1. 회원 검증
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        // 2. 도서 검증
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));

        // 3. 대여 가능 여부 확인
        if (book.getStatus() != Book.BookStatus.AVAILABLE) {
            throw new IllegalStateException("Book is not available: " + book.getTitle());
        }

        // 4. 최대 대여 권수 확인
        int activeCount = rentalRepository.countByMemberIdAndStatus(member.getId(), RentalStatus.ACTIVE);
        if (activeCount >= MAX_RENTAL_COUNT) {
            throw new IllegalStateException(
                    "Maximum rental limit exceeded. Current: " + activeCount + ", Max: " + MAX_RENTAL_COUNT);
        }

        // 5. 연체 확인
        List<Rental> overdueRentals = rentalRepository.findByMemberIdAndStatus(member.getId(), RentalStatus.OVERDUE);
        if (!overdueRentals.isEmpty()) {
            throw new IllegalStateException("Member has overdue rentals. Please return them first.");
        }

        // 6. 대여료 계산 (도서 대여료 × 대여일수)
        int rentalDays = request.getRentalDays() != null ? request.getRentalDays() : DEFAULT_RENTAL_DAYS;
        BigDecimal rentalFee = book.getRentalPrice().multiply(BigDecimal.valueOf(rentalDays));

        // 7. 등급별 할인 적용 (다형성 활용 - OrderService와 동일한 방식)
        BigDecimal discountedFee = applyDiscount(member, rentalFee);

        // 8. 대여 생성
        Rental rental = Rental.builder()
                .member(member)
                .book(book)
                .rentalDateTime(LocalDateTime.now())
                .dueDateTime(LocalDateTime.now().plusDays(rentalDays))
                .rentalFee(rentalFee)
                .discountedFee(discountedFee)
                .remarks(request.getRemarks())
                .build();

        Rental savedRental = rentalRepository.save(rental);
        return RentalResponse.from(savedRental);
    }

    /**
     * 회원 등급에 맞는 할인 정책 적용
     * OrderService의 applyDiscount()와 동일한 로직 (인터페이스 재사용)
     */
    private BigDecimal applyDiscount(Member member, BigDecimal price) {
        return discountPolicies.stream()
                .filter(policy -> policy.supports(member.getGrade()))
                .findFirst()
                .map(policy -> policy.calculateDiscount(price))
                .orElse(price);
    }

    @Transactional
    public RentalResponse returnBook(Long id) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rental not found with id: " + id));

        if (rental.getStatus() == RentalStatus.RETURNED) {
            throw new IllegalStateException("Book has already been returned.");
        }

        rental.returnBook();
        return RentalResponse.from(rental);
    }

    @Transactional
    public void delete(Long id) {
        if (!rentalRepository.existsById(id)) {
            throw new IllegalArgumentException("Rental not found with id: " + id);
        }
        rentalRepository.deleteById(id);
    }
}
