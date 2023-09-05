package com.example.controller;

import com.example.dto.BookDto;
import com.example.dto.CreateBookRequestDto;
import java.util.List;

public interface BookController {

    List<BookDto> getAll();

    BookDto getBookById(Long id);

    BookDto createBook(CreateBookRequestDto bookDto);
}
