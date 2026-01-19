package com.mokitooo.model.product.dto;

import java.math.BigDecimal;

public record CreateProductDTO(
        String name,
        int quantity,
        BigDecimal price
) {
    public static CreateProductDTOBuilder builder() {
        return new CreateProductDTOBuilder();
    }

    public static class CreateProductDTOBuilder {
        private String name;
        private int quantity;
        private BigDecimal price;

        public CreateProductDTOBuilder name(String name) {
            this.name = name;
            return this;
        }

        public CreateProductDTOBuilder quantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public CreateProductDTOBuilder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public CreateProductDTO build() {
            return new CreateProductDTO(name, quantity, price);
        }
    }
}
