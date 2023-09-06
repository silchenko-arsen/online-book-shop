package com.example.repository;

import com.example.dto.BookSearchParametersDto;
import com.example.model.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookSpecificationBuilder implements SpecificationBuilder<Book> {
    private final SpecificationProviderManager<Book> bookSpecificationProviderManager;

    @Override
    public Specification<Book> build(BookSearchParametersDto searchParametersDto) {
        Specification<Book> specification = Specification.where(null);
        if (searchParametersDto.getTitles() != null && searchParametersDto.getTitles().length > 0) {
            specification = specification.and(bookSpecificationProviderManager.getSpecificationProvider("title")
                    .getSpecification(searchParametersDto.getTitles()));
        }
        if (searchParametersDto.getAuthors() != null && searchParametersDto.getAuthors().length > 0) {
            specification = specification.and(bookSpecificationProviderManager.getSpecificationProvider("author")
                    .getSpecification(searchParametersDto.getAuthors()));
        }
        if (searchParametersDto.getIsbns() != null && searchParametersDto.getIsbns().length > 0) {
            specification = specification.and(bookSpecificationProviderManager.getSpecificationProvider("isbn")
                    .getSpecification(searchParametersDto.getIsbns()));
        }
        if (searchParametersDto.getPrices() != null && searchParametersDto.getPrices().length > 0) {
            specification = specification.and(bookSpecificationProviderManager.getSpecificationProvider("price")
                    .getSpecification(searchParametersDto.getPrices()));
        }
        return specification;
    }
}
