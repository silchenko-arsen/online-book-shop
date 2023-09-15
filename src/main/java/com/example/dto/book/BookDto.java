package com.example.dto.book;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class BookDto {
    private Long id;
    @NotBlank(message = "Title mustn't be null or empty")
    private String title;
    @NotBlank(message = "Author mustn't be null or empty")
    private String author;
    @NotBlank(message = "Isbn mustn't be null or empty")
    private String isbn;
    @NotNull(message = "Price mustn't be null")
    @Min(0)
    private BigDecimal price;
    private String description;
    private String coverImage;
}
