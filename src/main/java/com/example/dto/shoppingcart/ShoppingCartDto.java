package com.example.dto.shoppingcart;

import com.example.dto.cartitem.CartItemDto;
import jakarta.validation.constraints.Min;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;

@Data
public class ShoppingCartDto {
    private Long id;
    @Min(1)
    private Long userId;
    private Set<CartItemDto> cartItems = new HashSet<>();
}
