package com.mokitooo.persistance.product;

import com.mokitooo.exception.DataParsingException;
import com.mokitooo.exception.DataAccessException;
import com.mokitooo.exception.DataStructureException;
import com.mokitooo.model.product.Product;

import java.util.List;

public interface ProductPersistence {
    void persist(List<Product> product) throws IllegalArgumentException, DataAccessException;
    List<Product>getPersisted() throws DataAccessException, DataParsingException, DataStructureException;
}
