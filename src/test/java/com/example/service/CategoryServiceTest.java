package com.example.service;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.dto.category.CategoryDto;
import com.example.exception.EntityNotFoundException;
import com.example.mapper.CategoryMapper;
import com.example.model.Category;
import com.example.repository.CategoryRepository;
import com.example.service.impl.CategoryServiceImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category1;

    @BeforeEach
    void setUp() {
        category1 = new Category();
        category1.setId(1L);
        category1.setName("Fiction");
    }

    @Test
    public void findAll_WithValidPageable_ShouldReturnListOfCategories() {
        List<Category> categories = new ArrayList<>();
        categories.add(category1);
        Page<Category> categoriesPage = new PageImpl<>(categories);
        when(categoryRepository.findAll(any(Pageable.class))).thenReturn(categoriesPage);
        List<CategoryDto> expected = new ArrayList<>();
        expected.add(createCategoryDto());
        when(categoryMapper.toDto(category1))
                .thenReturn(createCategoryDto());
        List<CategoryDto> actual = categoryService.findAll(mock(Pageable.class));
        Assertions.assertEquals(expected, actual);
        verify(categoryRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    public void findById_WithInvalidId_ShouldThrowEntityNotFoundException() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> categoryService.findById(anyLong()));
        verify(categoryRepository, times(1)).findById(anyLong());
    }

    @Test
    public void saveCategory_WithValidCategoryDto_ShouldReturnSavedCategory() {
        CategoryDto expected = createCategoryDto();
        when(categoryMapper.toEntity(createCategoryDto())).thenReturn(category1);
        when(categoryRepository.save(category1)).thenReturn(category1);
        when(categoryMapper.toDto(category1)).thenReturn(expected);
        CategoryDto actual = categoryService.save(expected);
        Assertions.assertEquals(expected, actual);
        verify(categoryRepository, times(1)).save(category1);
    }

    @Test
    public void updateCategory_WithValidIdAndCategoryDto_ShouldReturnUpdatedCategory() {
        CategoryDto expected = createCategoryDto();
        Category updated = new Category();
        updated.setId(expected.getId());
        updated.setName("Fictions");
        when(categoryRepository.findById(expected.getId())).thenReturn(Optional.of(category1));
        when(categoryRepository.save(category1)).thenReturn(updated);
        when(categoryMapper.toDto(updated)).thenReturn(expected.setName("Fictions"));
        CategoryDto actual = categoryService.update(expected.getId(), expected);
        Assertions.assertEquals(expected, actual);
        verify(categoryRepository, times(1)).save(category1);
    }

    @Test
    public void deleteCategoryById_WithValidId_ShouldDeleteCategory() {
        categoryService.deleteById(anyLong());
        verify(categoryRepository, times(1)).deleteById(anyLong());
    }

    private static CategoryDto createCategoryDto() {
        return new CategoryDto()
                .setId(1L).setName("Fiction");
    }
}
