package com.example.dto.order;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrderShippingAddressDto {
    @NotBlank(message = "Shipping address mustn't be null or empty")
    private String shippingAddress;
}
