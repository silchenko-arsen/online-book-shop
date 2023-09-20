package com.example.service;

import com.example.dto.order.OrderDto;
import com.example.dto.order.OrderShippingAddressDto;
import com.example.dto.order.OrderStatusDto;
import com.example.dto.orderitem.OrderItemResponseDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderDto save(String authenticationName, OrderShippingAddressDto shippingAddress);

    List<OrderDto> getAllOrders(String authenticationName, Pageable pageable);

    List<OrderItemResponseDto> getAllOrderItems(Long orderId, Pageable pageable);

    OrderItemResponseDto getOrderItem(Long orderId, Long itemId);

    OrderDto updateOrderStatus(Long orderId, OrderStatusDto statusDto);
}
