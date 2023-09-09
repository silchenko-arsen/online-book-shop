package com.example.repository.book.specificationprovider;

import com.example.model.Book;
import com.example.repository.SpecificationProvider;
import java.util.Arrays;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class CoverImageSpecificationProvider implements SpecificationProvider<Book> {
    private static final String COVER_IMAGE = "coverImage";

    @Override
    public String getKey() {
        return COVER_IMAGE;
    }

    public Specification<Book> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) -> root
                .get(COVER_IMAGE).in(Arrays.stream(params).toArray());
    }
}
