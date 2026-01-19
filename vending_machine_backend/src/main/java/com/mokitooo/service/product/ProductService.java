package com.mokitooo.service.product;

import com.mokitooo.exception.ContainerFulfilledException;
import com.mokitooo.model.product.dto.CreateProductDTO;
import com.mokitooo.model.product.dto.ProductDTO;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    ProductDTO register(CreateProductDTO product) throws ContainerFulfilledException;
    List<ProductDTO> saveAll(Iterable<CreateProductDTO> products) throws IllegalArgumentException;
    void deleteById(UUID id);
    void deleteAll(Iterable<CreateProductDTO> products);
    List<ProductDTO> findAll();
}
