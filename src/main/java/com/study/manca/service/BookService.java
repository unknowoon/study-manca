package com.study.manca.service;

import com.study.manca.dto.BookRequest;
import com.study.manca.dto.BookResponse;
import com.study.manca.entity.Book;
import com.study.manca.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;

    // 전체 도서 조회 (GET)
    public List<BookResponse> findAll() {
        return bookRepository.findAll().stream()
                .map(BookResponse::from)
                .collect(Collectors.toList());
    }

    // 특정 도서 조회 (GET)
    public BookResponse findById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found with id: " + id));
        return BookResponse.from(book);
    }

    // 도서 생성 (POST)
    @Transactional
    public BookResponse create(BookRequest request) {
        Book book = request.toEntity();
        Book savedBook = bookRepository.save(book);
        return BookResponse.from(savedBook);
    }

    // 도서 전체 수정 (PUT)
    @Transactional
    public BookResponse update(Long id, BookRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found with id: " + id));

        book.setBookCode(request.getBookCode());
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setPublisher(request.getPublisher());
        book.setVolume(request.getVolume());
        book.setGenre(request.getGenre());
        book.setRentalPrice(request.getRentalPrice());
        book.setStatus(request.getStatus());
        book.setCondition(request.getCondition());
        book.setLocation(request.getLocation());
        book.setRemarks(request.getRemarks());

        return BookResponse.from(book);
    }

    // 도서 부분 수정 (PATCH)
    @Transactional
    public BookResponse updatePartial(Long id, BookRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found with id: " + id));

        if (request.getBookCode() != null) {
            book.setBookCode(request.getBookCode());
        }
        if (request.getTitle() != null) {
            book.setTitle(request.getTitle());
        }
        if (request.getAuthor() != null) {
            book.setAuthor(request.getAuthor());
        }
        if (request.getPublisher() != null) {
            book.setPublisher(request.getPublisher());
        }
        if (request.getVolume() != null) {
            book.setVolume(request.getVolume());
        }
        if (request.getGenre() != null) {
            book.setGenre(request.getGenre());
        }
        if (request.getRentalPrice() != null) {
            book.setRentalPrice(request.getRentalPrice());
        }
        if (request.getStatus() != null) {
            book.setStatus(request.getStatus());
        }
        if (request.getCondition() != null) {
            book.setCondition(request.getCondition());
        }
        if (request.getLocation() != null) {
            book.setLocation(request.getLocation());
        }
        if (request.getRemarks() != null) {
            book.setRemarks(request.getRemarks());
        }

        return BookResponse.from(book);
    }

    // 도서 삭제 (DELETE)
    @Transactional
    public void delete(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new IllegalArgumentException("Book not found with id: " + id);
        }
        bookRepository.deleteById(id);
    }
}
