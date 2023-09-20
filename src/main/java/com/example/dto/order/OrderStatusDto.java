package com.example.dto.order;

import com.example.model.Order;
import lombok.Data;

@Data
public class OrderStatusDto {
    private Order.Status status;
}
