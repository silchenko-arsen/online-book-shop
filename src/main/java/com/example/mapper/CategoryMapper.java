package com.example.mapper;

import com.example.config.MapperConfig;
import com.example.dto.category.CategoryDto;
import com.example.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface CategoryMapper {
    CategoryDto toDto(Category category);

    @Mapping(target = "id", ignore = true)
    Category toEntity(CategoryDto categoryDto);
}
