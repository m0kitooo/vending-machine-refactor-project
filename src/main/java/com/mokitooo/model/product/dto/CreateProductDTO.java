package com.mokitooo.model.product.dto;

import com.mokitooo.model.product.Product;

import java.math.BigDecimal;

public record CreateProductDTO(
        String name,
        int quantity,
        BigDecimal price
) {
    public Product toProduct() {
        return Product.builder()
                .name(name)
                .quantity(quantity)
                .price(price)
                .build();
    }
}
