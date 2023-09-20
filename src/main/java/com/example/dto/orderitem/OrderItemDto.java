package com.example.dto.orderitem;

import com.example.model.Book;
import com.example.model.Order;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class OrderItemDto {
    private Order order;
    private Book book;
    private int quantity;
    private BigDecimal price;
}
