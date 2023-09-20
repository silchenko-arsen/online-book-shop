package com.example.mapper;

import com.example.config.MapperConfig;
import com.example.dto.order.OrderDto;
import com.example.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, uses = OrderItemMapper.class)
public interface OrderMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "status", source = "status")
    OrderDto toDto(Order order);
}
