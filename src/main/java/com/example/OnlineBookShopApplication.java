package com.example;

import com.example.model.Book;
import com.example.service.BookService;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class OnlineBookShopApplication {
    @Autowired
    private BookService bookService;

    public static void main(String[] args) {
        SpringApplication.run(OnlineBookShopApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            Book book = new Book();
            book.setTitle("Harry Potter");
            book.setAuthor("J. K. Rowling");
            book.setIsbn("9781408856772");
            book.setDescription("Book about boy");
            book.setPrice(BigDecimal.valueOf(400));
            book.setCoverImage("https://content2.rozetka.com.ua/goods/images/big/241617101.jpg");
            bookService.save(book);
            System.out.println(bookService.findAll());
        };
    }
}

