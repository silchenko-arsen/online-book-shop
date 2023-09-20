package com.example.mapper;

import com.example.config.MapperConfig;
import com.example.dto.orderitem.OrderItemDto;
import com.example.dto.orderitem.OrderItemResponseDto;
import com.example.model.CartItem;
import com.example.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface OrderItemMapper {
    @Mapping(target = "book", source = "book")
    @Mapping(target = "quantity", source = "quantity")
    @Mapping(target = "price", source = "book.price")
    OrderItemDto toDto(CartItem cartItem);

    @Mapping(target = "bookId", source = "book.id")
    OrderItemResponseDto toResponseDto(OrderItem orderItem);
}
