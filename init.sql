-- 만화책 카페 관리 시스템 데이터베이스 초기화

-- Books 테이블
CREATE TABLE books (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(100) NOT NULL,
    stock INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Members 테이블 (Step 2 이후 사용)
CREATE TABLE members (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Rentals 테이블 (Step 2 이후 사용)
CREATE TABLE rentals (
    id BIGSERIAL PRIMARY KEY,
    book_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    rented_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    due_date TIMESTAMP NOT NULL,
    returned_at TIMESTAMP,
    FOREIGN KEY (book_id) REFERENCES books(id),
    FOREIGN KEY (member_id) REFERENCES members(id)
);

-- 인덱스 생성
CREATE INDEX idx_books_author ON books(author);
CREATE INDEX idx_books_title ON books(title);
CREATE INDEX idx_rentals_book_id ON rentals(book_id);
CREATE INDEX idx_rentals_member_id ON rentals(member_id);
CREATE INDEX idx_rentals_returned_at ON rentals(returned_at);

-- Books 샘플 데이터
INSERT INTO books (title, author, stock) VALUES
('원피스 1권', '오다 에이이치로', 5),
('원피스 2권', '오다 에이이치로', 3),
('원피스 3권', '오다 에이이치로', 4),
('나루토 1권', '키시모토 마사시', 3),
('나루토 2권', '키시모토 마사시', 2),
('나루토 3권', '키시모토 마사시', 0),
('슬램덩크 1권', '이노우에 다케히코', 2),
('슬램덩크 2권', '이노우에 다케히코', 4),
('헌터X헌터 1권', '토가시 요시히로', 1),
('헌터X헌터 2권', '토가시 요시히로', 0),
('강철의 연금술사 1권', '아라카와 히로무', 3),
('강철의 연금술사 2권', '아라카와 히로무', 5),
('데스노트 1권', '오바 츠구미', 6),
('데스노트 2권', '오바 츠구미', 4),
('드래곤볼 1권', '토리야마 아키라', 2),
('드래곤볼 2권', '토리야마 아키라', 0),
('진격의 거인 1권', '이사야마 하지메', 7),
('진격의 거인 2권', '이사야마 하지메', 3),
('귀멸의 칼날 1권', '고토게 코요하루', 8),
('귀멸의 칼날 2권', '고토게 코요하루', 5);

-- Members 샘플 데이터
INSERT INTO members (name, email, phone) VALUES
('김철수', 'chulsoo.kim@example.com', '010-1234-5678'),
('이영희', 'younghee.lee@example.com', '010-2345-6789'),
('박민수', 'minsoo.park@example.com', '010-3456-7890'),
('정수진', 'soojin.jung@example.com', '010-4567-8901'),
('최동욱', 'dongwook.choi@example.com', '010-5678-9012');

-- Rentals 샘플 데이터
INSERT INTO rentals (book_id, member_id, due_date, returned_at) VALUES
(1, 1, CURRENT_TIMESTAMP + INTERVAL '7 days', NULL),
(4, 2, CURRENT_TIMESTAMP + INTERVAL '7 days', NULL),
(7, 3, CURRENT_TIMESTAMP - INTERVAL '1 day', NULL),  -- 연체
(13, 1, CURRENT_TIMESTAMP - INTERVAL '10 days', CURRENT_TIMESTAMP - INTERVAL '5 days'),  -- 반납 완료
(17, 4, CURRENT_TIMESTAMP + INTERVAL '5 days', NULL);

-- 통계를 위한 뷰 (선택 사항)
CREATE VIEW book_stats AS
SELECT 
    COUNT(*) as total_books,
    SUM(stock) as total_stock,
    COUNT(CASE WHEN stock = 0 THEN 1 END) as out_of_stock_count,
    COUNT(CASE WHEN stock > 0 THEN 1 END) as available_count
FROM books;

-- 대여 중인 책 조회 뷰 (선택 사항)
CREATE VIEW active_rentals AS
SELECT 
    r.id as rental_id,
    b.title as book_title,
    b.author as book_author,
    m.name as member_name,
    r.rented_at,
    r.due_date,
    CASE 
        WHEN r.due_date < CURRENT_TIMESTAMP THEN true
        ELSE false
    END as is_overdue
FROM rentals r
JOIN books b ON r.book_id = b.id
JOIN members m ON r.member_id = m.id
WHERE r.returned_at IS NULL;

-- 테이블 코멘트
COMMENT ON TABLE books IS '만화책 정보';
COMMENT ON TABLE members IS '회원 정보';
COMMENT ON TABLE rentals IS '대여 정보';

COMMENT ON COLUMN books.id IS '책 ID';
COMMENT ON COLUMN books.title IS '책 제목';
COMMENT ON COLUMN books.author IS '저자';
COMMENT ON COLUMN books.stock IS '재고 수량';

COMMENT ON COLUMN members.id IS '회원 ID';
COMMENT ON COLUMN members.name IS '회원 이름';
COMMENT ON COLUMN members.email IS '이메일';
COMMENT ON COLUMN members.phone IS '전화번호';

COMMENT ON COLUMN rentals.id IS '대여 ID';
COMMENT ON COLUMN rentals.book_id IS '책 ID';
COMMENT ON COLUMN rentals.member_id IS '회원 ID';
COMMENT ON COLUMN rentals.rented_at IS '대여 시작 시간';
COMMENT ON COLUMN rentals.due_date IS '반납 예정일';
COMMENT ON COLUMN rentals.returned_at IS '실제 반납 시간';
