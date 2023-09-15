package com.example.dto.book;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookSearchParametersDto {
    private String[] titles;
    private String[] authors;
    private String[] isbns;
    private String[] prices;
    private String[] descriptions;
    private String[] coverImages;
}
