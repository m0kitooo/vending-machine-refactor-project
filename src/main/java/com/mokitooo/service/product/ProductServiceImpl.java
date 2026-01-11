package com.mokitooo.service.product;

import com.mokitooo.exception.ContainerFulfilledException;
import com.mokitooo.model.product.Product;
import com.mokitooo.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
//    private final int StorageSize;

    @Override
    public Product register(Product product) throws ContainerFulfilledException {
        if (findAll().size() >= 21) throw new ContainerFulfilledException("Container is already fulfilled maxsize 20");

        if (product.getId() != null && productRepository.findById(product.getId()) != null) {
            throw new IllegalArgumentException("Product with ID " + product.getId() + " already exists.");
        }
        return productRepository.save(product);
    }

    @Override
    public List<Product> saveAll(Iterable<Product> products) throws IllegalArgumentException {
        return productRepository.saveAll(products);
    }

    public void deleteById(UUID id) {
        productRepository.deleteById(id);
    }

    public void deleteAll(Iterable<Product> products) {
        productRepository.deleteAll(products);
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

}
