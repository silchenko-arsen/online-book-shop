package com.example.repository;

import com.example.model.Book;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    @Query("SELECT DISTINCT b FROM Book b JOIN b.categories c WHERE c.id=:categoryId")
    List<Book> findAllByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT DISTINCT b FROM Book b "
            + "LEFT JOIN FETCH b.categories "
            + "WHERE b.isDeleted = false")
    Page<Book> findAllWithCategories(Pageable pageable);

    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.categories"
            + " WHERE b.isDeleted=false AND b.id =:id")
    Optional<Book> findByIdWithCategories(Long id);
}
