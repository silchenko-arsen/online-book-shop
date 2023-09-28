package com.example.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.model.Book;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookRepositoryIntegrationTest {
    @Autowired
    private BookRepository bookRepository;

    @Test
    @Sql(scripts = "classpath:database/add-for-book-category-tests.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/delete-for-book-category-tests.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllByCategoryId_ReturnsOneBook() {
        long id = 1L;
        Book book = new Book();
        book.setId(id);
        book.setTitle("Book 1");
        book.setIsbn("13245768");
        book.setAuthor("Author 1");
        book.setPrice(BigDecimal.valueOf(350.05));
        List<Book> expected = new ArrayList<>();
        expected.add(book);
        List<Book> actual = bookRepository.findAllByCategoryId(id);
        assertEquals(expected, actual);
    }

    @Test
    @Sql(scripts = "classpath:database/add-for-book-category-tests.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/delete-for-book-category-tests.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllByCategoryId_ReturnsEmpty() {
        long id = 5;
        List<Book> books = bookRepository.findAllByCategoryId(id);
        assertEquals(0, books.size());
    }
}
