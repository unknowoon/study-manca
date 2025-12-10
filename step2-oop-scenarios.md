# Step 2: 객체지향 개념 적용 시나리오

만화카페 도메인에서 상속, 다형성, 인터페이스, 추상 클래스, 접근 제어자를 실제 비즈니스 요건에 적용하는 시나리오입니다.

---

## 시나리오 1: 회원 등급별 할인 정책

### 비즈니스 요건

만화카페에서 회원 등급에 따라 다른 할인율을 적용하고 싶습니다.

| 등급 | 조건 | 할인율 |
|------|------|--------|
| BRONZE | 기본 | 0% |
| SILVER | 누적 이용 10회 이상 | 5% |
| GOLD | 누적 이용 30회 이상 | 10% |
| VIP | 누적 이용 100회 이상 | 15% |

### 나쁜 설계: if-else 지옥

```java
public class OrderService {
    public BigDecimal calculatePrice(Order order) {
        BigDecimal price = order.getTotalPrice();
        Member member = order.getMember();

        // 등급이 추가될 때마다 여기를 수정해야 함
        if (member.getGrade() == MemberGrade.BRONZE) {
            return price;
        } else if (member.getGrade() == MemberGrade.SILVER) {
            return price.multiply(new BigDecimal("0.95"));
        } else if (member.getGrade() == MemberGrade.GOLD) {
            return price.multiply(new BigDecimal("0.90"));
        } else if (member.getGrade() == MemberGrade.VIP) {
            return price.multiply(new BigDecimal("0.85"));
        }
        return price;
    }
}
```

**문제점:**
- 새로운 등급 추가 시 Service 코드 수정 필요 (OCP 위반)
- 할인 로직이 Service에 노출됨
- 테스트하기 어려움

### 좋은 설계: 인터페이스 + 다형성

```java
// 1. 할인 정책 인터페이스 정의
public interface DiscountPolicy {
    BigDecimal calculateDiscount(BigDecimal price);
    boolean supports(MemberGrade grade);
}

// 2. 등급별 구현체
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

public class SilverDiscountPolicy implements DiscountPolicy {
    private static final BigDecimal DISCOUNT_RATE = new BigDecimal("0.95");

    @Override
    public BigDecimal calculateDiscount(BigDecimal price) {
        return price.multiply(DISCOUNT_RATE);
    }

    @Override
    public boolean supports(MemberGrade grade) {
        return grade == MemberGrade.SILVER;
    }
}

// Gold, VIP도 동일한 패턴으로 구현

// 3. Service에서 다형성 활용
@Service
public class OrderService {
    private final List<DiscountPolicy> discountPolicies;  // 모든 정책 주입

    public BigDecimal calculatePrice(Order order) {
        BigDecimal price = order.getTotalPrice();
        MemberGrade grade = order.getMember().getGrade();

        return discountPolicies.stream()
            .filter(policy -> policy.supports(grade))
            .findFirst()
            .map(policy -> policy.calculateDiscount(price))
            .orElse(price);
    }
}
```

### 적용된 개념

| 개념 | 적용 |
|------|------|
| **인터페이스** | `DiscountPolicy` - 할인 계산의 공통 규약 |
| **다형성** | `List<DiscountPolicy>`로 여러 구현체를 동일하게 처리 |
| **OCP (개방-폐쇄 원칙)** | 새 등급 추가 시 구현체만 추가, Service 수정 불필요 |

---

## 시나리오 2: 알림 발송 시스템

### 비즈니스 요건

다음 상황에 회원에게 알림을 발송합니다:
- 대여 도서 반납 기한 D-1 알림
- 연체 시 알림
- 주문 완료 알림

알림 채널은 SMS, 이메일, 푸시 알림 중 회원이 선택합니다.

### 설계: 추상 클래스 + 템플릿 메서드 패턴

```java
// 1. 알림 발송의 공통 로직을 추상 클래스로
public abstract class NotificationSender {

    // 템플릿 메서드: 전체 흐름 정의
    public final void send(Member member, String message) {
        if (!isEnabled(member)) {
            return;  // 해당 채널 비활성화 시 스킵
        }

        String formattedMessage = formatMessage(message);
        doSend(member, formattedMessage);
        logNotification(member, message);
    }

    // 공통 로직: 메시지 포맷팅
    protected String formatMessage(String message) {
        return "[만화카페] " + message;
    }

    // 공통 로직: 로그 기록
    private void logNotification(Member member, String message) {
        log.info("알림 발송 - 회원: {}, 메시지: {}", member.getName(), message);
    }

    // 추상 메서드: 하위 클래스가 구현
    protected abstract boolean isEnabled(Member member);
    protected abstract void doSend(Member member, String message);
}

// 2. 채널별 구현
public class SmsNotificationSender extends NotificationSender {
    private final SmsClient smsClient;

    @Override
    protected boolean isEnabled(Member member) {
        return member.isSmsEnabled() && member.getPhone() != null;
    }

    @Override
    protected void doSend(Member member, String message) {
        smsClient.send(member.getPhone(), message);
    }
}

public class EmailNotificationSender extends NotificationSender {
    private final EmailClient emailClient;

    @Override
    protected boolean isEnabled(Member member) {
        return member.isEmailEnabled() && member.getEmail() != null;
    }

    @Override
    protected void doSend(Member member, String message) {
        emailClient.send(member.getEmail(), "만화카페 알림", message);
    }
}

public class PushNotificationSender extends NotificationSender {
    private final PushClient pushClient;

    @Override
    protected boolean isEnabled(Member member) {
        return member.isPushEnabled() && member.getDeviceToken() != null;
    }

    @Override
    protected void doSend(Member member, String message) {
        pushClient.send(member.getDeviceToken(), message);
    }
}

// 3. 모든 채널로 알림 발송
@Service
public class NotificationService {
    private final List<NotificationSender> senders;

    public void notifyAll(Member member, String message) {
        senders.forEach(sender -> sender.send(member, message));
    }
}
```

### 적용된 개념

| 개념 | 적용 |
|------|------|
| **추상 클래스** | `NotificationSender` - 공통 로직 + 추상 메서드 |
| **상속** | 각 채널이 추상 클래스 상속 |
| **템플릿 메서드 패턴** | `send()` 메서드가 전체 흐름 제어 |
| **다형성** | `List<NotificationSender>`로 모든 채널 동일 처리 |
| **접근 제어자** | `protected` - 하위 클래스에서만 접근/오버라이드 |

### 인터페이스 vs 추상 클래스 선택 기준

이 시나리오에서 **추상 클래스**를 선택한 이유:
- 공통 로직이 있음 (`formatMessage`, `logNotification`)
- 실행 순서가 정해져 있음 (검증 → 발송 → 로깅)
- 하위 클래스는 일부만 구현하면 됨

---

## 시나리오 3: 좌석 요금 계산

### 비즈니스 요건

좌석 타입별로 시간당 요금이 다릅니다.

| 좌석 타입 | 시간당 요금 | 특이사항 |
|-----------|------------|----------|
| REGULAR | 2,000원 | - |
| PREMIUM | 3,000원 | 3시간 이상 시 10% 추가 할인 |
| COUPLE | 5,000원 | 2인 기준, 1인 추가 시 +2,000원 |
| ROOM | 10,000원 | 최소 2시간 이용 필수 |

### 설계: 인터페이스 + 전략 패턴

```java
// 1. 요금 계산 전략 인터페이스
public interface SeatPricingStrategy {
    BigDecimal calculatePrice(int hours, int persons);
    SeatType getSeatType();
}

// 2. 좌석별 요금 전략 구현
@Component
public class RegularSeatPricing implements SeatPricingStrategy {
    private static final BigDecimal HOURLY_RATE = new BigDecimal("2000");

    @Override
    public BigDecimal calculatePrice(int hours, int persons) {
        return HOURLY_RATE.multiply(BigDecimal.valueOf(hours));
    }

    @Override
    public SeatType getSeatType() {
        return SeatType.REGULAR;
    }
}

@Component
public class PremiumSeatPricing implements SeatPricingStrategy {
    private static final BigDecimal HOURLY_RATE = new BigDecimal("3000");
    private static final BigDecimal LONG_STAY_DISCOUNT = new BigDecimal("0.90");

    @Override
    public BigDecimal calculatePrice(int hours, int persons) {
        BigDecimal basePrice = HOURLY_RATE.multiply(BigDecimal.valueOf(hours));

        // 3시간 이상 시 10% 할인
        if (hours >= 3) {
            return basePrice.multiply(LONG_STAY_DISCOUNT);
        }
        return basePrice;
    }

    @Override
    public SeatType getSeatType() {
        return SeatType.PREMIUM;
    }
}

@Component
public class CoupleSeatPricing implements SeatPricingStrategy {
    private static final BigDecimal HOURLY_RATE = new BigDecimal("5000");
    private static final BigDecimal EXTRA_PERSON_RATE = new BigDecimal("2000");
    private static final int BASE_PERSONS = 2;

    @Override
    public BigDecimal calculatePrice(int hours, int persons) {
        BigDecimal basePrice = HOURLY_RATE.multiply(BigDecimal.valueOf(hours));

        // 2인 초과 시 추가 요금
        if (persons > BASE_PERSONS) {
            int extraPersons = persons - BASE_PERSONS;
            BigDecimal extraCharge = EXTRA_PERSON_RATE
                .multiply(BigDecimal.valueOf(extraPersons))
                .multiply(BigDecimal.valueOf(hours));
            return basePrice.add(extraCharge);
        }
        return basePrice;
    }

    @Override
    public SeatType getSeatType() {
        return SeatType.COUPLE;
    }
}

@Component
public class RoomSeatPricing implements SeatPricingStrategy {
    private static final BigDecimal HOURLY_RATE = new BigDecimal("10000");
    private static final int MINIMUM_HOURS = 2;

    @Override
    public BigDecimal calculatePrice(int hours, int persons) {
        // 최소 2시간 적용
        int chargedHours = Math.max(hours, MINIMUM_HOURS);
        return HOURLY_RATE.multiply(BigDecimal.valueOf(chargedHours));
    }

    @Override
    public SeatType getSeatType() {
        return SeatType.ROOM;
    }
}

// 3. 전략 선택 및 실행
@Service
public class SeatPricingService {
    private final Map<SeatType, SeatPricingStrategy> strategyMap;

    public SeatPricingService(List<SeatPricingStrategy> strategies) {
        this.strategyMap = strategies.stream()
            .collect(Collectors.toMap(
                SeatPricingStrategy::getSeatType,
                Function.identity()
            ));
    }

    public BigDecimal calculatePrice(Seat seat, int hours, int persons) {
        SeatPricingStrategy strategy = strategyMap.get(seat.getType());
        if (strategy == null) {
            throw new IllegalArgumentException("Unknown seat type: " + seat.getType());
        }
        return strategy.calculatePrice(hours, persons);
    }
}
```

### 적용된 개념

| 개념 | 적용 |
|------|------|
| **인터페이스** | `SeatPricingStrategy` - 요금 계산 규약 |
| **다형성** | `Map<SeatType, SeatPricingStrategy>`로 런타임 전략 선택 |
| **전략 패턴** | 좌석 타입별 다른 계산 로직 캡슐화 |
| **접근 제어자** | `private static final` - 상수 캡슐화 |

---

## 시나리오 4: 대여 규칙 검증

### 비즈니스 요건

대여 전 여러 규칙을 검증해야 합니다:
1. 회원이 존재하는가?
2. 도서가 대여 가능한 상태인가?
3. 회원이 최대 대여 권수(3권)를 초과하지 않았는가?
4. 회원이 연체 중인 도서가 없는가?
5. 회원이 블랙리스트에 등록되지 않았는가?

### 설계: 인터페이스 + 체인 오브 리스폰시빌리티 패턴

```java
// 1. 검증 규칙 인터페이스
public interface RentalValidator {
    void validate(Member member, Book book);
    int getOrder();  // 실행 순서
}

// 2. 각 규칙 구현
@Component
public class BookAvailabilityValidator implements RentalValidator {

    @Override
    public void validate(Member member, Book book) {
        if (book.getStatus() != BookStatus.AVAILABLE) {
            throw new IllegalStateException(
                "도서 '" + book.getTitle() + "'은(는) 현재 대여할 수 없습니다. " +
                "상태: " + book.getStatus()
            );
        }
    }

    @Override
    public int getOrder() {
        return 1;
    }
}

@Component
public class MaxRentalCountValidator implements RentalValidator {
    private static final int MAX_RENTAL_COUNT = 3;
    private final RentalRepository rentalRepository;

    @Override
    public void validate(Member member, Book book) {
        int activeCount = rentalRepository.countByMemberIdAndStatus(
            member.getId(),
            RentalStatus.ACTIVE
        );

        if (activeCount >= MAX_RENTAL_COUNT) {
            throw new IllegalStateException(
                "최대 " + MAX_RENTAL_COUNT + "권까지만 대여 가능합니다. " +
                "현재 대여 중: " + activeCount + "권"
            );
        }
    }

    @Override
    public int getOrder() {
        return 2;
    }
}

@Component
public class OverdueCheckValidator implements RentalValidator {
    private final RentalRepository rentalRepository;

    @Override
    public void validate(Member member, Book book) {
        List<Rental> overdueRentals = rentalRepository.findByMemberIdAndStatus(
            member.getId(),
            RentalStatus.OVERDUE
        );

        if (!overdueRentals.isEmpty()) {
            throw new IllegalStateException(
                "연체 중인 도서가 있어 대여할 수 없습니다. " +
                "연체 도서 수: " + overdueRentals.size()
            );
        }
    }

    @Override
    public int getOrder() {
        return 3;
    }
}

@Component
public class BlacklistValidator implements RentalValidator {
    private final BlacklistRepository blacklistRepository;

    @Override
    public void validate(Member member, Book book) {
        if (blacklistRepository.existsByMemberId(member.getId())) {
            throw new IllegalStateException("서비스 이용이 제한된 회원입니다.");
        }
    }

    @Override
    public int getOrder() {
        return 0;  // 가장 먼저 체크
    }
}

// 3. 검증 실행기
@Component
public class RentalValidatorChain {
    private final List<RentalValidator> validators;

    public RentalValidatorChain(List<RentalValidator> validators) {
        // 순서대로 정렬
        this.validators = validators.stream()
            .sorted(Comparator.comparingInt(RentalValidator::getOrder))
            .collect(Collectors.toList());
    }

    public void validateAll(Member member, Book book) {
        for (RentalValidator validator : validators) {
            validator.validate(member, book);  // 실패 시 예외 발생
        }
    }
}

// 4. Service에서 사용
@Service
public class RentalService {
    private final RentalValidatorChain validatorChain;

    @Transactional
    public RentalResponse create(RentalRequest request) {
        Member member = memberRepository.findById(request.getMemberId())
            .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        Book book = bookRepository.findById(request.getBookId())
            .orElseThrow(() -> new IllegalArgumentException("도서를 찾을 수 없습니다."));

        // 모든 검증 규칙 실행
        validatorChain.validateAll(member, book);

        // 대여 처리
        Rental rental = Rental.builder()
            .member(member)
            .book(book)
            .rentalDateTime(LocalDateTime.now())
            .dueDateTime(LocalDateTime.now().plusDays(7))
            .build();

        return RentalResponse.from(rentalRepository.save(rental));
    }
}
```

### 적용된 개념

| 개념 | 적용 |
|------|------|
| **인터페이스** | `RentalValidator` - 검증 규칙 규약 |
| **다형성** | `List<RentalValidator>`로 모든 규칙 순회 |
| **단일 책임 원칙** | 각 Validator는 하나의 규칙만 담당 |
| **개방-폐쇄 원칙** | 새 규칙 추가 시 구현체만 추가 |

---

## 시나리오 5: Entity 공통 기능 상속

### 비즈니스 요건

모든 Entity에 다음 기능이 필요합니다:
- 생성일시, 수정일시 자동 관리
- 삭제 시 실제 삭제가 아닌 소프트 삭제 (deletedAt 기록)
- 삭제된 데이터는 조회에서 제외

### 설계: 추상 클래스 + 상속

```java
// 1. 기본 Entity (현재 구현)
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public abstract class BaseEntity {

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}

// 2. 소프트 삭제 지원 Entity (확장)
@MappedSuperclass
@Getter
public abstract class SoftDeletableEntity extends BaseEntity {

    @Column
    private LocalDateTime deletedAt;

    @Column
    private boolean deleted = false;

    // protected: 하위 클래스와 같은 패키지에서만 호출 가능
    protected void softDelete() {
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    protected void restore() {
        this.deleted = false;
        this.deletedAt = null;
    }

    public boolean isDeleted() {
        return deleted;
    }
}

// 3. 소프트 삭제가 필요한 Entity
@Entity
@Where(clause = "deleted = false")  // 조회 시 삭제된 데이터 자동 제외
public class Member extends SoftDeletableEntity {
    // ...

    public void withdraw() {
        softDelete();  // 회원 탈퇴 시 소프트 삭제
    }
}

@Entity
@Where(clause = "deleted = false")
public class Book extends SoftDeletableEntity {
    // ...

    public void discard() {
        softDelete();  // 폐기 시 소프트 삭제
    }
}

// 4. 소프트 삭제가 필요 없는 Entity (기존 BaseEntity 사용)
@Entity
public class Rental extends BaseEntity {
    // 대여 기록은 실제 삭제해도 됨
}
```

### 적용된 개념

| 개념 | 적용 |
|------|------|
| **상속** | `SoftDeletableEntity extends BaseEntity` |
| **추상 클래스** | `BaseEntity`, `SoftDeletableEntity` - 직접 생성 불가 |
| **접근 제어자** | `protected` - 외부에서 직접 삭제 방지, 메서드를 통해서만 삭제 |
| **캡슐화** | 삭제 로직을 Entity 내부에 숨김 |

### 접근 제어자의 중요성

```java
// 나쁜 예: public setter
public class Member {
    private boolean deleted;

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;  // 아무나 삭제 상태 변경 가능!
    }
}

// 좋은 예: 비즈니스 메서드로 제어
public class Member {
    private boolean deleted;

    // public: 외부에서 호출 가능
    public void withdraw() {
        // 탈퇴 전 검증 로직 추가 가능
        validateWithdrawal();
        softDelete();
    }

    // protected: 상속 관계에서만 호출
    protected void softDelete() {
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    // private: 내부에서만 사용
    private void validateWithdrawal() {
        // 탈퇴 가능 조건 검증
    }
}
```

---

## 정리: 언제 무엇을 사용하나?

| 상황 | 선택 | 이유 |
|------|------|------|
| 여러 구현체가 **동일한 계약**을 따를 때 | 인터페이스 | 다형성 활용, 느슨한 결합 |
| **공통 로직**을 공유할 때 | 추상 클래스 | 코드 재사용, 템플릿 메서드 |
| **is-a 관계**일 때 | 상속 | Member is a SoftDeletableEntity |
| **can-do 관계**일 때 | 인터페이스 | DiscountPolicy can calculate discount |
| 외부에서 **직접 변경 방지** | private + 비즈니스 메서드 | 캡슐화, 불변성 보장 |
| 하위 클래스에서만 사용 | protected | 상속 관계 내부 구현 |

---

## 실습 제안

위 시나리오 중 하나를 선택하여 직접 구현해보세요.

**추천 순서:**
1. **시나리오 4 (대여 규칙 검증)** - 현재 과제와 직접 연결됨
2. **시나리오 1 (할인 정책)** - 인터페이스 + 다형성 이해에 좋음
3. **시나리오 3 (좌석 요금)** - 전략 패턴 연습

구현 후 다음을 확인해보세요:
- [ ] 새로운 정책/규칙 추가 시 기존 코드를 수정해야 하는가?
- [ ] 각 클래스가 단일 책임을 가지는가?
- [ ] 테스트 코드 작성이 쉬운가?
