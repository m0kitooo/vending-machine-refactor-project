package com.mokitooo.persistance.product;

import com.mokitooo.exception.PersistenceException;
import com.mokitooo.model.product.Product;

import java.util.List;

public interface ProductPersistence {
    void persist(List<Product> product) throws PersistenceException;
    List<Product>getPersisted() throws PersistenceException;
}
