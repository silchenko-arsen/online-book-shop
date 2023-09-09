package com.example.repository.book.specificationprovider;

import com.example.dto.BookSearchParametersDto;
import com.example.model.Book;
import com.example.repository.SpecificationBuilder;
import com.example.repository.SpecificationProviderManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookSpecificationBuilder implements SpecificationBuilder<Book> {
    private static final String AUTHOR = "author";
    private static final String COVER_IMAGE = "coverImage";
    private static final String DESCRIPTION = "description";
    private static final String ISBN = "isbn";
    private static final String PRICE = "price";
    private static final String TITLE = "title";
    private final SpecificationProviderManager<Book> bookSpecificationProviderManager;

    @Override
    public Specification<Book> build(BookSearchParametersDto searchParametersDto) {
        Specification<Book> specification = Specification.where(null);
        if (searchParametersDto.getTitles() != null
                && searchParametersDto.getTitles().length > 0) {
            specification = specification
                    .and(bookSpecificationProviderManager.getSpecificationProvider(TITLE)
                    .getSpecification(searchParametersDto.getTitles()));
        }
        if (searchParametersDto.getAuthors() != null
                && searchParametersDto.getAuthors().length > 0) {
            specification = specification
                    .and(bookSpecificationProviderManager.getSpecificationProvider(AUTHOR)
                    .getSpecification(searchParametersDto.getAuthors()));
        }
        if (searchParametersDto.getIsbns() != null
                && searchParametersDto.getIsbns().length > 0) {
            specification = specification
                    .and(bookSpecificationProviderManager.getSpecificationProvider(ISBN)
                    .getSpecification(searchParametersDto.getIsbns()));
        }
        if (searchParametersDto.getPrices() != null
                && searchParametersDto.getPrices().length > 0) {
            specification = specification
                    .and(bookSpecificationProviderManager.getSpecificationProvider(PRICE)
                    .getSpecification(searchParametersDto.getPrices()));
        }
        if (searchParametersDto.getDescriptions() != null
                && searchParametersDto.getDescriptions().length > 0) {
            specification = specification
                    .and(bookSpecificationProviderManager.getSpecificationProvider(DESCRIPTION)
                            .getSpecification(searchParametersDto.getDescriptions()));
        }
        if (searchParametersDto.getCoverImages() != null
                && searchParametersDto.getCoverImages().length > 0) {
            specification = specification
                    .and(bookSpecificationProviderManager.getSpecificationProvider(COVER_IMAGE)
                            .getSpecification(searchParametersDto.getCoverImages()));
        }
        return specification;
    }
}
