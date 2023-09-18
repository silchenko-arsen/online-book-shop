package com.example.service;

import com.example.dto.book.BookDto;
import com.example.dto.book.BookDtoWithoutCategoryIds;
import com.example.dto.book.BookSearchParametersDto;
import com.example.dto.book.CreateBookRequestDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookDto save(CreateBookRequestDto bookDto);

    BookDto update(Long id, BookDto bookDto);

    List<BookDto> findAll(Pageable pageable);

    BookDto findById(Long id);

    void deleteById(Long id);

    List<BookDto> search(BookSearchParametersDto bookSearchParametersDto);

    List<BookDtoWithoutCategoryIds> findAllByCategoryId(Long categoryId);
}
