package com.mokitooo.repository.product;

import com.mokitooo.exception.EntityNotFoundException;
import com.mokitooo.model.product.Product;
import lombok.NonNull;

import java.util.*;

public class InMemoryProductRepository implements ProductRepository {
    private final Map<UUID, Product> products;

    public InMemoryProductRepository() {
        this.products = new HashMap<>();
    }

    public InMemoryProductRepository(Map<UUID, Product> products) {
        this.products = new HashMap<>(products);
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }

    @Override
    public Product findById(@NonNull UUID id) throws EntityNotFoundException {
        Product product = products.get(id);
        if (product == null) {
            throw new EntityNotFoundException("Product with ID " + id + " not found.");
        }
        return product;
    }

    @Override
    public Product save(Product product) throws IllegalArgumentException {
        if (product.getId() == null) {
            product.setId(UUID.randomUUID());
        }
        products.put(product.getId(), product);
        return product;
    }

    @Override
    public List<Product> saveAll(@NonNull Iterable<Product> products) throws IllegalArgumentException {
         products.forEach(this::save);
         List<Product> result = new ArrayList<>();
         products.forEach(result::add);
         return result;
    }

    @Override
    public void deleteById(@NonNull UUID id) throws EntityNotFoundException {
        if (products.remove(id) == null) {
            throw new IllegalArgumentException("Product with ID " + id + " does not exist.");
        }
    }

    @Override
    public void deleteAll(Iterable<Product> products) {
        products.forEach(product -> this.products.remove(product.getId()));
    }
}
