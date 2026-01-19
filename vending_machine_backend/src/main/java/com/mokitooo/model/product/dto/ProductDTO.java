package com.mokitooo.model.product.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductDTO(UUID id, String name, int quantity, BigDecimal price) {
    public ProductDTO decreaseQuantity() {
        return new ProductDTO(
                id,
                name,
                quantity - 1,
                price
        );
    }
}