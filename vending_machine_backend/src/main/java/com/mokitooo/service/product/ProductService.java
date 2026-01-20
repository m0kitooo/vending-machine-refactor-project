package com.mokitooo.service.product;

import com.mokitooo.exception.ContainerFulfilledException;
import com.mokitooo.exception.DataAccessException;
import com.mokitooo.exception.DataParsingException;
import com.mokitooo.exception.DataStructureException;
import com.mokitooo.model.product.dto.CreateProductDTO;
import com.mokitooo.model.product.dto.ProductDTO;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    ProductDTO register(CreateProductDTO product) throws IllegalArgumentException, ContainerFulfilledException;
    ProductDTO update(ProductDTO product) throws IllegalArgumentException;
    List<ProductDTO> saveAll(Iterable<CreateProductDTO> products) throws IllegalArgumentException;
    void deleteById(UUID id) throws IllegalArgumentException;
    void deleteAll(Iterable<UUID> productIds) throws IllegalArgumentException;
    List<ProductDTO> findAll();
    void reloadData() throws DataAccessException, DataParsingException, DataStructureException;
    void persistProducts(Iterable<CreateProductDTO> products) throws IllegalArgumentException;
}
