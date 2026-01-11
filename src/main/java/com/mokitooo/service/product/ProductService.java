package com.mokitooo.service.product;

import com.mokitooo.exception.ContainerFulfilledException;
import com.mokitooo.model.product.Product;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    Product register(Product product) throws ContainerFulfilledException;
    List<Product> saveAll(Iterable<Product> products) throws IllegalArgumentException;
    void deleteById(UUID id);
    void deleteAll(Iterable<Product> products);
    List<Product> findAll();
}
