package com.example.service;

import com.example.dto.BookDto;
import com.example.dto.CreateBookRequestDto;
import com.example.model.Book;
import java.util.List;

public interface BookService {
    BookDto save(CreateBookRequestDto bookDto);

    List<BookDto> findAll();

    BookDto findById(Long id);
}
