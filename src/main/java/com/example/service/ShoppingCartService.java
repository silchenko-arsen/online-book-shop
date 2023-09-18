package com.example.service;

import com.example.dto.cartitem.CartItemDto;
import com.example.dto.cartitem.CartItemQuantityDto;
import com.example.dto.cartitem.CartItemRequestDto;
import com.example.dto.shoppingcart.ShoppingCartDto;

public interface ShoppingCartService {
    ShoppingCartDto getShoppingCartByUserEmail(String email);
    CartItemDto addCartItem(String email, CartItemRequestDto cartItemRequestDto);

    CartItemDto updateBookQuantity(Long cartItemId, CartItemQuantityDto cartItemQuantityDto);

    void deleteCartItemById(Long cartItemId);
}
