package com.mokitooo.repository.product;

import com.mokitooo.exception.EntityNotFoundException;
import com.mokitooo.model.product.Product;

import java.util.*;

public class SyncInMemoryProductRepository implements ProductRepository {
    private Map<UUID, Product> products;

    public SyncInMemoryProductRepository() {
        this(Map.of());
    }

    public SyncInMemoryProductRepository(Map<UUID, Product> products) {
        this.products = new LinkedHashMap<>(products);
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }

    @Override
    public Product findById(UUID id) throws EntityNotFoundException, IllegalArgumentException {
        if (id == null) {
            throw new IllegalArgumentException("id can't be null");
        }
        Product product = products.get(id);
        if (product == null) {
            throw new EntityNotFoundException("Product with ID " + id + " not found.");
        }
        return product;
    }

    @Override
    public Product save(Product product) throws IllegalArgumentException {
        if (product == null) {
            throw new IllegalArgumentException("product can't be null");
        }
        if (product.getId() == null) {
            product.setId(UUID.randomUUID());
        }
        products.put(product.getId(), product);
        return product;
    }

    @Override
    public List<Product> saveAll(Iterable<Product> productsToSave) throws IllegalArgumentException {
        if (productsToSave == null) {
            throw new IllegalArgumentException("productsToSave can't be null");
        }
        Map<UUID, Product> backup = new LinkedHashMap<>(this.products);
        List<Product> savedProducts = new ArrayList<>();

        try {
            for (Product product : productsToSave) {
                savedProducts.add(this.save(product));
            }
            return savedProducts;
        } catch (Exception e) {
            this.products = backup;
            throw e;
        }
    }

    @Override
    public void deleteById(UUID id) throws EntityNotFoundException, IllegalArgumentException {
        if (id == null) {
            throw new IllegalArgumentException("id can't be null");
        }
        if (products.remove(id) == null) {
            throw new EntityNotFoundException("Product with ID " + id + " does not exist.");
        }
    }

    @Override
    public void deleteAll(Iterable<UUID> productIds) throws EntityNotFoundException, IllegalArgumentException {
        if (productIds == null) {
            throw new IllegalArgumentException("productIds can't be null");
        }
        Map<UUID, Product> backup = new LinkedHashMap<>(this.products);

        try {
            for (UUID id : productIds) {
                this.deleteById(id);
            }
        } catch (Exception e) {
            this.products = backup;
            throw e;
        }
    }
}
