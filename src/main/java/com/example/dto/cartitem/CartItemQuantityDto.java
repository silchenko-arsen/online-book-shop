package com.example.dto.cartitem;

import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CartItemQuantityDto {
    @Min(1)
    private int quantity;
}
