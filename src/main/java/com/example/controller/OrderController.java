package com.example.controller;

import com.example.dto.order.OrderDto;
import com.example.dto.order.OrderShippingAddressDto;
import com.example.dto.order.OrderStatusDto;
import com.example.dto.orderitem.OrderItemResponseDto;
import com.example.exception.EntityNotFoundException;
import com.example.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order management", description = "Endpoints for managing order")
@RequiredArgsConstructor
@RestController
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Create a new order", description = "Create a new order")
    OrderDto createOrder(@RequestBody @Valid OrderShippingAddressDto orderShippingAddressDto) {
        return orderService.save(getAuthenticationName(), orderShippingAddressDto);
    }

    @GetMapping
    @Operation(summary = "Get all orders", description = "Get all orders")
    List<OrderDto> getAllOrders(Pageable pageable) {
        return orderService.getAllOrders(getAuthenticationName(), pageable);
    }

    @GetMapping("/{orderId}/items")
    @Operation(summary = "Get order items in order by order id",
            description = "Retrieve a list of order items for a specific order")
    List<OrderItemResponseDto> getAllItems(@PathVariable Long orderId, Pageable pageable) {
        return orderService.getAllOrderItems(orderId, pageable);
    }

    @GetMapping("/{orderId}/items/{id}")
    @Operation(summary = "Get a specific item in order",
            description = "View a specific item in order")
    OrderItemResponseDto getOrderItem(@PathVariable Long orderId, @PathVariable Long id) {
        return orderService.getOrderItem(orderId, id);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Update order status",
            description = "Update order status by only admin")
    OrderDto updateOrderStatus(@PathVariable Long id,
                               @RequestBody @Valid OrderStatusDto statusDto) {
        return orderService.updateOrderStatus(id,statusDto);
    }

    private String getAuthenticationName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        throw new EntityNotFoundException(
                "Can't find authentication name by authentication " + authentication);
    }
}
