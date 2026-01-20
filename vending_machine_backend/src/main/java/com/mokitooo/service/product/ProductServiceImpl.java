package com.mokitooo.service.product;

import com.mokitooo.config.AppConfig;
import com.mokitooo.exception.ContainerFulfilledException;
import com.mokitooo.exception.DataAccessException;
import com.mokitooo.mapper.ProductMapper;
import com.mokitooo.model.product.Product;
import com.mokitooo.model.product.dto.CreateProductDTO;
import com.mokitooo.model.product.dto.ProductDTO;
import com.mokitooo.persistance.product.ProductPersistence;
import com.mokitooo.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final int storageSize = AppConfig.INSTANCE.getProductContainerCapacity();
    private final ProductMapper productMapper = new ProductMapper();
    private final ProductRepository productRepository;
    private final ProductPersistence productPersistence;

    @Override
    public ProductDTO register(CreateProductDTO createProductDTO)
            throws IllegalArgumentException, ContainerFulfilledException {
        if (createProductDTO == null) {
            throw new IllegalArgumentException("createProductDTO can't be null");
        }

        if (findAll().size() >= storageSize)
            throw new ContainerFulfilledException("Container is already fulfilled maxsize " + storageSize);

        Product product = productMapper.toEntity(createProductDTO);

        if (product.getId() != null && productRepository.findById(product.getId()) != null) {
            throw new IllegalArgumentException("Product with ID " + product.getId() + " already exists.");
        }
        return productMapper.toDTO(productRepository.save(product));
    }

    public ProductDTO update(ProductDTO product) throws IllegalArgumentException {
        if (product == null) {
            throw new IllegalArgumentException("product can't be null");
        }
        return productMapper.toDTO(productRepository.save(productMapper.toEntity(product)));
    }

    @Override
    public List<ProductDTO> saveAll(Iterable<CreateProductDTO> products) throws IllegalArgumentException {
        if (products == null) {
            throw new IllegalArgumentException("product can't be null");
        }

        Iterable<Product> productEntities = StreamSupport.stream(products.spliterator(), false)
                .map(productMapper::toEntity)
                .toList();
        
        return productRepository.saveAll(productEntities).stream()
                .map(productMapper::toDTO)
                .toList();
    }

    public void deleteById(UUID id) throws IllegalArgumentException {
        if (id == null) {
            throw new IllegalArgumentException("id can't be null");
        }
        productRepository.deleteById(id);
    }

    public void deleteAll(Iterable<UUID> productIds) throws IllegalArgumentException {
        if (productIds == null) {
            throw new IllegalArgumentException("productIds can't be null");
        }
        productRepository.deleteAll(productIds);
    }

    public List<ProductDTO> findAll() {
        return productRepository.findAll().stream()
                .map(productMapper::toDTO)
                .toList();
    }

    @Override
    public void reloadData() {
        try {
            productRepository.deleteAll(findAll().stream().map(ProductDTO::id).toList());
            productRepository.saveAll(productPersistence.getPersisted());
        } catch (DataAccessException e) {

        }
    }

    @Override
    public void persistProducts(Iterable<CreateProductDTO> products) throws IllegalArgumentException {
        if (products == null) {
            throw new IllegalArgumentException("products can't be null");
        }
        productPersistence.persist(StreamSupport.stream(products.spliterator(), false)
                .map(productMapper::toEntity).toList());
    }
}
