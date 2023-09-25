package com.example.dto.book;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class BookSearchParametersDto {
    private String[] titles;
    private String[] authors;
    private String[] isbns;
    private String[] prices;
    private String[] descriptions;
    private String[] coverImages;
}
