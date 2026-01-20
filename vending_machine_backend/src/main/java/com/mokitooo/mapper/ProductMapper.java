package com.mokitooo.mapper;

import com.mokitooo.model.product.Product;
import com.mokitooo.model.product.dto.CreateProductDTO;
import com.mokitooo.model.product.dto.ProductDTO;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public final class ProductMapper {
    public Product toEntity(CreateProductDTO dto) {
        return Product.builder()
                .name(dto.name())
                .quantity(dto.quantity())
                .price(dto.price())
                .build();
    }

    public Product toEntity(ProductDTO dto) {
        return Product.builder()
                .id(dto.id())
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

    public CreateProductDTO toDto(ProductDTO dto) {
        return new CreateProductDTO(dto.name(), dto.quantity(), dto.price());
    }

    public static List<UUID> toIds(Iterable<ProductDTO> products) {
        return StreamSupport.stream(products.spliterator(), false)
                .map(ProductDTO::id)
                .collect(Collectors.toList());
    }
}
