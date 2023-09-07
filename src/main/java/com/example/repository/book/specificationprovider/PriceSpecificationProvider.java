package com.example.repository.book.specificationprovider;

import com.example.model.Book;
import com.example.repository.SpecificationProvider;
import java.util.Arrays;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class PriceSpecificationProvider implements SpecificationProvider<Book> {
    private static final String PRICE = "price";

    @Override
    public String getKey() {
        return PRICE;
    }

    public Specification<Book> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) -> root
                .get(PRICE).in(Arrays.stream(params).toArray());
    }
}
