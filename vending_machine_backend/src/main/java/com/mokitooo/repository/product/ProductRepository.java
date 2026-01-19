package com.mokitooo.repository.product;

import com.mokitooo.exception.EntityNotFoundException;
import com.mokitooo.model.product.Product;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;

public interface ProductRepository {
    List<Product> findAll();
    Product findById(@NonNull UUID id) throws EntityNotFoundException;
    Product save(Product product) throws IllegalArgumentException;
    List<Product> saveAll(Iterable<Product> products) throws IllegalArgumentException;
    void deleteById(@NonNull UUID id) throws EntityNotFoundException;
    void deleteAll(Iterable<UUID> productIds) throws EntityNotFoundException;
}
