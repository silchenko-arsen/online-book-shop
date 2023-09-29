package com.example.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.dto.book.BookDto;
import com.example.dto.book.BookDtoWithoutCategoryIds;
import com.example.dto.book.CreateBookRequestDto;
import com.example.exception.EntityNotFoundException;
import com.example.mapper.BookMapper;
import com.example.model.Book;
import com.example.model.Category;
import com.example.repository.BookRepository;
import com.example.repository.CategoryRepository;
import com.example.service.impl.BookServiceImpl;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {
    private static final long ID = 1;
    private Book book;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookServiceImpl bookService;

    @BeforeEach
    public void setUp() {
        book = new Book();
        book.setId(ID);
        book.setTitle("Book 1");
        book.setAuthor("Author 1");
        book.setIsbn("13245768");
        book.setPrice(BigDecimal.valueOf(350.05));
    }

    @AfterEach
    public void cleanUp() {
        book = null;
    }

    @Test
    public void saveBook_WithValidFields_ShouldReturnValidBook() {
        CreateBookRequestDto bookRequestDto = createBook();
        BookDto expected = createBookDto();
        when(bookMapper.toEntity(bookRequestDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(expected);
        BookDto actual = bookService.save(bookRequestDto);
        assertEquals(expected, actual);
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    public void findAll_WithValidPageable_ShouldReturnListOfBooks() {
        List<Book> bookList = new ArrayList<>();
        bookList.add(book);
        Page<Book> bookPage = new PageImpl<>(bookList);
        BookDto bookDto = createBookDto();
        List<BookDto> expected = new ArrayList<>();
        expected.add(bookDto);
        when(bookRepository.findAll(any(Pageable.class))).thenReturn(bookPage);
        when(bookMapper.toDto(book)).thenReturn(bookDto);
        List<BookDto> actual = bookService.findAll(mock(Pageable.class));
        assertEquals(expected, actual);
        verify(bookRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    public void findById_WithValidId_ShouldReturnBook() {
        when(bookRepository.findById(ID)).thenReturn(Optional.of(book));
        BookDto expected = createBookDto();
        when(bookMapper.toDto(book)).thenReturn(expected);
        BookDto actual = bookService.findById(ID);
        assertEquals(expected, actual);
        verify(bookRepository, times(1)).findById(ID);
    }

    @Test
    public void findById_WithInvalidId_ShouldThrowEntityNotFoundException() {
        when(bookRepository.findById(ID)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> bookService.findById(ID));
        verify(bookRepository, times(1)).findById(ID);
    }

    @Test
    public void deleteById_WithValidId_ShouldDeleteBook() {
        bookService.deleteById(ID);
        verify(bookRepository, times(1)).deleteById(ID);
    }

    @Test
    public void updateById_WithValidId_ShouldUpdateBook() {
        Category category = new Category();
        category.setId(ID);
        category.setName("Fictions");
        when(bookRepository.findById(ID)).thenReturn(Optional.of(book));
        when(bookRepository.save(book)).thenReturn(book);
        when(categoryRepository.findById(ID)).thenReturn(Optional.of(category));
        BookDto bookDto = createBookDto();
        bookService.update(ID, bookDto);
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    public void findAllByCategoryId_WithValidCategoryId_ShouldReturnListOfBooks() {
        List<Book> bookList = new ArrayList<>();
        bookList.add(book);
        when(bookRepository.findAllByCategoryId(anyLong())).thenReturn(bookList);
        BookDtoWithoutCategoryIds bookDtoWithoutCategoryIds = new BookDtoWithoutCategoryIds()
                .setId(1L)
                .setTitle("Book 1")
                .setAuthor("Author 1")
                .setIsbn("13245768")
                .setPrice(BigDecimal.valueOf(350.05));
        List<BookDtoWithoutCategoryIds> expected = new ArrayList<>();
        expected.add(bookDtoWithoutCategoryIds);
        when(bookMapper.toDtoWithoutCategories(any(Book.class)))
                .thenReturn(bookDtoWithoutCategoryIds);
        List<BookDtoWithoutCategoryIds> actual = bookService.findAllByCategoryId(anyLong());
        assertEquals(expected, actual);
        verify(bookRepository, times(1)).findAllByCategoryId(anyLong());
    }

    private CreateBookRequestDto createBook() {
        return new CreateBookRequestDto()
                .setTitle("Book 1")
                .setAuthor("Author 1")
                .setIsbn("13245768")
                .setPrice(BigDecimal.valueOf(350.05));
    }

    private BookDto createBookDto() {
        return new BookDto()
                .setId(1L)
                .setTitle("Book 1")
                .setAuthor("Author 1")
                .setIsbn("13245768")
                .setPrice(BigDecimal.valueOf(350.05))
                .setCategoryIds(List.of(1L));
    }
}
