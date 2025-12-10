package com.study.manca.dto;

import com.study.manca.entity.Book;
import com.study.manca.entity.Book.BookCondition;
import com.study.manca.entity.Book.BookStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "도서 응답")
@Getter
public class BookResponse {

    @Schema(description = "도서 ID", example = "1")
    private final Long id;

    @Schema(description = "도서코드", example = "MH-001-001")
    private final String bookCode;

    @Schema(description = "제목", example = "원피스")
    private final String title;

    @Schema(description = "작가", example = "오다 에이이치로")
    private final String author;

    @Schema(description = "출판사", example = "대원씨아이")
    private final String publisher;

    @Schema(description = "권수", example = "1")
    private final Integer volume;

    @Schema(description = "장르", example = "액션")
    private final String genre;

    @Schema(description = "대여료", example = "1000")
    private final BigDecimal rentalPrice;

    @Schema(description = "대여상태", example = "AVAILABLE")
    private final BookStatus status;

    @Schema(description = "책 상태", example = "GOOD")
    private final BookCondition condition;

    @Schema(description = "서가위치", example = "A-01")
    private final String location;

    @Schema(description = "비고", example = "인기도서")
    private final String remarks;

    @Schema(description = "생성일시", example = "2025-01-24T10:30:00")
    private final LocalDateTime createdAt;

    @Schema(description = "수정일시", example = "2025-01-24T10:30:00")
    private final LocalDateTime updatedAt;

    public BookResponse(Book book) {
        this.id = book.getId();
        this.bookCode = book.getBookCode();
        this.title = book.getTitle();
        this.author = book.getAuthor();
        this.publisher = book.getPublisher();
        this.volume = book.getVolume();
        this.genre = book.getGenre();
        this.rentalPrice = book.getRentalPrice();
        this.status = book.getStatus();
        this.condition = book.getCondition();
        this.location = book.getLocation();
        this.remarks = book.getRemarks();
        this.createdAt = book.getCreatedAt();
        this.updatedAt = book.getUpdatedAt();
    }

    public static BookResponse from(Book book) {
        return new BookResponse(book);
    }
}
