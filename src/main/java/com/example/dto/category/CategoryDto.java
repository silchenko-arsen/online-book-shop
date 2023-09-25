package com.example.dto.category;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CategoryDto {
    private Long id;
    @NotBlank(message = "Name mustn't be null or empty")
    private String name;
    private String description;
}
