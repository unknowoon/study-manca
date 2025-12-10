# Step 2: 생각해볼 질문 - 답변

## 1. 왜 Controller에서 직접 Repository를 호출하지 않고 Service를 거치나요?

### 핵심 이유: 관심사의 분리 (Separation of Concerns)

각 계층은 자신의 역할에만 집중해야 합니다.

| 계층 | 역할 | 알아야 하는 것 |
|------|------|---------------|
| Controller | HTTP 처리 | 요청/응답 형식, URL 매핑 |
| Service | 비즈니스 로직 | 업무 규칙, 트랜잭션 |
| Repository | 데이터 접근 | SQL, 데이터베이스 |

### 실제 문제 상황

**Controller에서 직접 Repository를 호출하면:**

```
// 나쁜 예: 대여 로직이 Controller에 있음
@PostMapping("/rentals")
public ResponseEntity<?> rent(@RequestBody RentalRequest request) {
    Member member = memberRepository.findById(request.getMemberId()).orElseThrow();
    Book book = bookRepository.findById(request.getBookId()).orElseThrow();

    // 비즈니스 로직이 Controller에 노출됨
    if (book.getStatus() != BookStatus.AVAILABLE) {
        throw new IllegalStateException("대여 불가");
    }

    int count = rentalRepository.countByMemberIdAndStatus(member.getId(), ACTIVE);
    if (count >= 3) {
        throw new IllegalStateException("최대 3권까지만 대여 가능");
    }

    Rental rental = Rental.builder()...build();
    rentalRepository.save(rental);
    return ResponseEntity.ok(rental);
}
```

**문제점:**
1. 같은 대여 로직을 다른 Controller에서 사용하려면 코드 복사 필요
2. 대여 규칙이 바뀌면 모든 Controller 수정 필요
3. 트랜잭션 관리가 어려움
4. 테스트하기 어려움 (HTTP 요청을 만들어야 함)

### Service를 거치면

```
// 좋은 예: 비즈니스 로직은 Service에
@Service
public class RentalService {
    @Transactional
    public RentalResponse rent(RentalRequest request) {
        // 모든 대여 관련 로직이 여기에 집중
        // 다른 곳에서도 이 메서드만 호출하면 됨
    }
}

@RestController
public class RentalController {
    @PostMapping("/rentals")
    public ResponseEntity<?> rent(@RequestBody RentalRequest request) {
        return ResponseEntity.ok(rentalService.rent(request));  // 깔끔!
    }
}
```

### 추가 이점

1. **트랜잭션 관리**: `@Transactional`은 Service 계층에서 관리
2. **재사용성**: 웹, 배치, 스케줄러 등 어디서든 Service 호출 가능
3. **테스트 용이**: Service만 단위 테스트 가능
4. **보안**: Controller에서 민감한 데이터 로직 노출 방지

---

## 2. DTO를 사용하는 이유는?

### DTO (Data Transfer Object)란?

계층 간 데이터 전송을 위한 객체. Entity와 분리하여 사용합니다.

### Entity를 직접 사용하면 안 되는 이유

**1. 보안 문제**

```java
@Entity
public class Member {
    private Long id;
    private String name;
    private String email;
    private String password;      // API 응답에 노출되면 안됨!
    private String socialNumber;  // 주민번호 노출 위험!
}
```

Entity를 그대로 반환하면 모든 필드가 JSON으로 노출됩니다.

**2. 순환 참조 문제**

```java
@Entity
public class Member {
    @OneToMany(mappedBy = "member")
    private List<Rental> rentals;  // Member → Rental
}

@Entity
public class Rental {
    @ManyToOne
    private Member member;  // Rental → Member
}
```

Jackson이 JSON 변환 시 무한 루프 발생: `Member → Rental → Member → Rental → ...`

**3. API 스펙 변경의 영향**

Entity 필드명을 바꾸면 API 응답도 바뀜 → 클라이언트 코드 전부 수정 필요

```java
// Entity 필드명 변경
private String userName;  // name → userName 으로 변경

// API 응답도 바뀜
{ "userName": "홍길동" }  // 기존: { "name": "홍길동" }
// 모든 클라이언트 코드 수정 필요!
```

### DTO를 사용하면

```java
// 응답용 DTO - 필요한 필드만 선택적으로 노출
public class MemberResponse {
    private Long id;
    private String name;
    private String email;
    // password, socialNumber 없음!

    public static MemberResponse from(Member entity) {
        return new MemberResponse(entity.getId(), entity.getName(), entity.getEmail());
    }
}
```

**장점:**
1. 민감 정보 제외 가능
2. API 스펙과 Entity 독립적으로 관리
3. 순환 참조 방지
4. API 버전별로 다른 DTO 사용 가능

---

## 3. `@Transactional(readOnly = true)`를 Service 클래스에 붙이고, 수정 메서드에만 `@Transactional`을 붙이는 이유는?

### 트랜잭션이란?

데이터베이스 작업의 논리적 단위. "모두 성공하거나, 모두 실패하거나" (All or Nothing)

### 패턴 설명

```java
@Service
@Transactional(readOnly = true)  // 클래스 레벨: 기본값
public class RentalService {

    public List<RentalResponse> findAll() {
        // readOnly = true 적용됨
    }

    public RentalResponse findById(Long id) {
        // readOnly = true 적용됨
    }

    @Transactional  // 메서드 레벨: 오버라이드
    public RentalResponse create(RentalRequest request) {
        // readOnly = false (기본값)
    }

    @Transactional
    public void delete(Long id) {
        // readOnly = false
    }
}
```

### readOnly = true의 이점

**1. 성능 최적화**

```
readOnly = true 일 때:
- Dirty Checking 생략 (변경 감지 안 함)
- 스냅샷 저장 안 함
- 플러시 생략
→ 메모리 절약, 속도 향상
```

**2. 의도 명확화**

코드를 보는 사람이 "이 메서드는 조회만 한다"는 것을 바로 알 수 있음

**3. 실수 방지**

```java
@Transactional(readOnly = true)
public MemberResponse findById(Long id) {
    Member member = memberRepository.findById(id).orElseThrow();
    member.setName("실수로 수정");  // DB에 반영 안됨! (안전)
    return MemberResponse.from(member);
}
```

### 왜 클래스에 readOnly, 메서드에 쓰기용?

**통계적 이유:** 대부분의 Service 메서드는 조회입니다.

```
일반적인 Service 메서드 비율:
- 조회 (findAll, findById, search...): 70~80%
- 수정 (create, update, delete): 20~30%
```

기본값을 `readOnly = true`로 설정하고, 수정이 필요한 메서드만 `@Transactional`로 오버라이드하는 것이 효율적입니다.

---

## 4. 왜 Repository를 인터페이스로 만들고 구현체는 Spring이 생성하게 하나요?

### 인터페이스만 작성하면 되는 마법

```java
// 개발자가 작성하는 코드: 인터페이스만!
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    List<Member> findByNameContaining(String keyword);
}

// 구현체는? Spring Data JPA가 런타임에 자동 생성!
```

### 이렇게 동작하는 이유

**1. 다형성 활용**

```java
@Service
public class MemberService {
    private final MemberRepository memberRepository;  // 인터페이스 타입

    // 실제로는 Spring이 생성한 프록시 객체가 주입됨
    // 개발자는 구현체가 뭔지 몰라도 됨
}
```

**2. 결합도 감소**

```
강한 결합 (나쁜 예):
MemberService → MemberRepositoryImpl (구체 클래스)
               → JdbcMemberRepository
               → MyBatisMemberRepository
변경 시 Service 코드 수정 필요

느슨한 결합 (좋은 예):
MemberService → MemberRepository (인터페이스)
                    ↑
              Spring이 구현체 주입
변경 시 Service 코드 수정 불필요
```

**3. 테스트 용이성**

```java
// 테스트 시 가짜 구현체 사용 가능
@MockBean
private MemberRepository memberRepository;

@Test
void test() {
    // 가짜 동작 정의
    given(memberRepository.findById(1L))
        .willReturn(Optional.of(new Member("테스트")));

    // 실제 DB 없이 테스트 가능!
}
```

**4. 메서드 이름으로 쿼리 자동 생성**

```java
// 메서드 이름만으로 SQL 자동 생성
List<Member> findByNameAndEmail(String name, String email);
// → SELECT * FROM member WHERE name = ? AND email = ?

List<Member> findByNameContainingOrderByCreatedAtDesc(String keyword);
// → SELECT * FROM member WHERE name LIKE '%keyword%' ORDER BY created_at DESC
```

### 정리: Repository Pattern의 이점

| 이점 | 설명 |
|------|------|
| 추상화 | 데이터 접근 기술(JPA, MyBatis 등)을 숨김 |
| 테스트 | Mock 객체로 대체 가능 |
| 유지보수 | 구현 기술 변경 시 Service 코드 수정 불필요 |
| 생산성 | 메서드 이름만으로 쿼리 생성 |
