package com.example.dto.cartitem;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CartItemDto {
    private Long id;
    @Min(1)
    private Long bookId;
    @NotBlank
    private String bookTitle;
    @Min(1)
    private int quantity;
}
