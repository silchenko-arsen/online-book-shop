package com.example.service;

import com.example.dto.BookDto;
import com.example.dto.BookSearchParametersDto;
import com.example.dto.CreateBookRequestDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookDto save(CreateBookRequestDto bookDto);

    BookDto update(BookDto bookDto, Long id);

    List<BookDto> findAll(Pageable pageable);

    BookDto findById(Long id);

    void deleteById(Long id);

    List<BookDto> search(BookSearchParametersDto bookSearchParametersDto);
}
