package com.study.manca.dto;

import com.study.manca.entity.Book;
import com.study.manca.entity.Book.BookCondition;
import com.study.manca.entity.Book.BookStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Schema(description = "도서 등록/수정 요청")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookRequest {

    @Schema(description = "도서코드", example = "MH-001-001")
    private String bookCode;

    @Schema(description = "제목", example = "원피스")
    private String title;

    @Schema(description = "작가", example = "오다 에이이치로")
    private String author;

    @Schema(description = "출판사", example = "대원씨아이")
    private String publisher;

    @Schema(description = "권수", example = "1")
    private Integer volume;

    @Schema(description = "장르", example = "액션")
    private String genre;

    @Schema(description = "대여료", example = "1000")
    private BigDecimal rentalPrice;

    @Schema(description = "대여상태", example = "AVAILABLE")
    private BookStatus status;

    @Schema(description = "책 상태", example = "GOOD")
    private BookCondition condition;

    @Schema(description = "서가위치", example = "A-01")
    private String location;

    @Schema(description = "비고", example = "인기도서")
    private String remarks;

    public Book toEntity() {
        return Book.builder()
                .bookCode(bookCode)
                .title(title)
                .author(author)
                .publisher(publisher)
                .volume(volume)
                .genre(genre)
                .rentalPrice(rentalPrice != null ? rentalPrice : new BigDecimal("1000"))
                .status(status != null ? status : BookStatus.AVAILABLE)
                .condition(condition != null ? condition : BookCondition.GOOD)
                .location(location)
                .remarks(remarks)
                .build();
    }
}
