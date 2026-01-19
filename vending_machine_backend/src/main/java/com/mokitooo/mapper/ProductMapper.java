package com.mokitooo.mapper;

import com.mokitooo.model.product.Product;
import com.mokitooo.model.product.dto.CreateProductDTO;
import com.mokitooo.model.product.dto.ProductDTO;

public class ProductMapper {
    public Product toEntity(CreateProductDTO dto) {
        return Product.builder()
                .name(dto.name())
                .quantity(dto.quantity())
                .price(dto.price())
                .build();
    }

    public ProductDTO toDTO(Product product) {
        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getQuantity(),
                product.getPrice()
        );
    }
}
