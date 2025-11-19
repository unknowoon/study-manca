# Step 1: Spring Boot REST API 기초 실습

## 학습 목표
- REST API 개념 이해
- HTTP 메서드와 CRUD 연산 매핑
- 멱등성(Idempotent) 개념 이해
- HTTP 상태 코드 활용
- RESTful URL 설계 원칙

---

## 프로젝트 구조

```
manca/
├── src/
│   ├── main/
│   │   ├── java/com/study/manca/
│   │   │   ├── MancaApplication.java          # 메인 애플리케이션
│   │   │   ├── controller/                    # REST 컨트롤러
│   │   │   │   └── UserController.java
│   │   │   ├── service/                       # 비즈니스 로직
│   │   │   │   └── UserService.java
│   │   │   ├── repository/                    # 데이터 접근 계층
│   │   │   │   └── UserRepository.java
│   │   │   ├── entity/                        # JPA 엔티티
│   │   │   │   └── User.java
│   │   │   └── dto/                           # 데이터 전송 객체
│   │   │       ├── UserRequest.java
│   │   │       └── UserResponse.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── build.gradle
└── docker-compose.yml                         # PostgreSQL 설정
```

---

## 실습 내용

### 1. REST API 개념

REST(Representational State Transfer)는 HTTP를 사용해 데이터를 주고받는 웹 API 설계 방식입니다.

**핵심 원칙:**
- 자원(Resource)을 URL로 표현
- 행위(Action)를 HTTP 메서드로 표현

### 2. HTTP 메서드와 CRUD 매핑

| HTTP 메서드 | CRUD | 설명 | 멱등성 |
|------------|------|------|--------|
| GET | Read | 리소스 조회 | O |
| POST | Create | 리소스 생성 | X |
| PUT | Update | 리소스 전체 수정 | O |
| PATCH | Update | 리소스 부분 수정 | X |
| DELETE | Delete | 리소스 삭제 | O |

### 3. RESTful URL 설계 예시

```
GET    /api/users          # 전체 사용자 목록 조회
GET    /api/users/{id}     # 특정 사용자 조회
POST   /api/users          # 사용자 생성
PUT    /api/users/{id}     # 사용자 정보 전체 수정
PATCH  /api/users/{id}     # 사용자 정보 부분 수정
DELETE /api/users/{id}     # 사용자 삭제
```

### 4. HTTP 상태 코드

**2xx - 성공**
- `200 OK`: 요청 성공
- `201 Created`: 리소스 생성 성공
- `204 No Content`: 성공했지만 반환 데이터 없음

**4xx - 클라이언트 오류**
- `400 Bad Request`: 잘못된 요청
- `404 Not Found`: 리소스를 찾을 수 없음
- `409 Conflict`: 리소스 충돌

**5xx - 서버 오류**
- `500 Internal Server Error`: 서버 내부 오류

---

## 실습 과정

### Step 1-1: 환경 설정 확인

**사용 기술:**
- Java 17
- Spring Boot 3.5.7
- Spring Data JPA
- PostgreSQL
- Lombok

**데이터베이스 실행:**
```bash
docker-compose up -d
```

### Step 1-2: Entity 작성

`User.java` - JPA 엔티티 클래스 작성

### Step 1-3: DTO 작성

- `UserRequest.java` - 요청 데이터 전송 객체
- `UserResponse.java` - 응답 데이터 전송 객체

### Step 1-4: Repository 작성

`UserRepository.java` - Spring Data JPA Repository

### Step 1-5: Service 작성

`UserService.java` - 비즈니스 로직 계층

### Step 1-6: Controller 작성

`UserController.java` - REST API 엔드포인트 정의

### Step 1-7: API 테스트

cURL 또는 Postman을 사용하여 API 테스트

---

## 다음 단계

- Swagger를 이용한 API 문서화
- 예외 처리 및 Validation
- 단위 테스트 작성

