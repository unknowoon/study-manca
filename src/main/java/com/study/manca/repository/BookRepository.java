package com.study.manca.repository;

import com.study.manca.entity.Book;
import com.study.manca.entity.Book.BookStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByStatus(BookStatus status);
    List<Book> findByGenre(String genre);
}
