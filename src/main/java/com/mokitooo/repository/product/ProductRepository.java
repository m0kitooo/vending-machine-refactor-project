package com.mokitooo.repository.product;

import com.mokitooo.model.product.Product;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;

public interface ProductRepository {
    List<Product> findAll();
    Product findById(@NonNull UUID id);
    Product save(Product product) throws IllegalArgumentException;
    List<Product> saveAll(Iterable<Product> products) throws IllegalArgumentException;
    void deleteById(@NonNull UUID id);
    void deleteAll(Iterable<Product> products);
}
