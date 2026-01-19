package com.mokitooo.service.product;

import com.mokitooo.config.AppConfig;
import com.mokitooo.exception.ContainerFulfilledException;
import com.mokitooo.mapper.ProductMapper;
import com.mokitooo.model.product.Product;
import com.mokitooo.model.product.dto.CreateProductDTO;
import com.mokitooo.model.product.dto.ProductDTO;
import com.mokitooo.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final int storageSize = AppConfig.INSTANCE.getProductContainerCapacity();

    @Override
    public ProductDTO register(CreateProductDTO createProductDTO) throws ContainerFulfilledException {
        if (findAll().size() > storageSize) throw new ContainerFulfilledException("Container is already fulfilled maxsize " + storageSize);

        Product product = createProductDTO.toProduct();

        if (product.getId() != null && productRepository.findById(product.getId()) != null) {
            throw new IllegalArgumentException("Product with ID " + product.getId() + " already exists.");
        }
        return productMapper.toDTO(productRepository.save(product));
    }

    @Override
    public List<ProductDTO> saveAll(Iterable<CreateProductDTO> products) throws IllegalArgumentException {
        Iterable<Product> productEntities = StreamSupport.stream(products.spliterator(), false)
                .map(productMapper::toEntity)
                .toList();
        
        return productRepository.saveAll(productEntities).stream()
                .map(productMapper::toDTO)
                .toList();
    }

    public void deleteById(UUID id) {
        productRepository.deleteById(id);
    }

    public void deleteAll(Iterable<CreateProductDTO> products) {
        Iterable<Product> productEntities = StreamSupport.stream(products.spliterator(), false)
                .map(CreateProductDTO::toProduct)
                .toList();
        
        productRepository.deleteAll(productEntities);
    }

    public List<ProductDTO> findAll() {
        return productRepository.findAll().stream()
                .map(productMapper::toDTO)
                .toList();
    }

}
