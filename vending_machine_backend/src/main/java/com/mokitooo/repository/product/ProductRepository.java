package com.mokitooo.repository.product;

import com.mokitooo.exception.EntityNotFoundException;
import com.mokitooo.model.product.Product;

import java.util.List;
import java.util.UUID;

public interface ProductRepository {
    List<Product> findAll();
    Product findById(UUID id) throws EntityNotFoundException, IllegalArgumentException;
    Product save(Product product) throws IllegalArgumentException;
    List<Product> saveAll(Iterable<Product> products) throws IllegalArgumentException;
    void deleteById(UUID id) throws EntityNotFoundException, IllegalArgumentException;
    void deleteAll(Iterable<UUID> productIds) throws EntityNotFoundException, IllegalArgumentException;
}
