package com.example.mapper;

import com.example.config.MapperConfig;
import com.example.dto.cartitem.CartItemDto;
import com.example.dto.cartitem.CartItemRequestDto;
import com.example.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface CartItemMapper {
    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "bookTitle", source = "book.title")
    CartItemDto toDto(CartItem cartItem);

    @Mapping(target = "book.id", source = "bookId")
    CartItem toEntity(CartItemRequestDto cartItemRequestDto);
}
