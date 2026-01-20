package com.mokitooo.persistance.product;

import com.mokitooo.config.AppConfig;
import com.mokitooo.exception.PersistenceException;
import com.mokitooo.model.product.Product;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

public class FileProductPersistence implements ProductPersistence {
    private final String filename = AppConfig.INSTANCE.getProductdataFilepath();

    public void persist(List<Product> products) throws PersistenceException {
        try (FileWriter writer = new FileWriter(filename)) {
            for (Product product : products) {
                StringJoiner joiner = new StringJoiner(System.lineSeparator())
                        .add(product.getName())
                        .add(String.valueOf(product.getQuantity()))
                        .add(String.valueOf(product.getPrice()));

                writer.write(joiner + System.lineSeparator());
            }
        } catch (IOException e) {
            throw new PersistenceException(e.getMessage());
        }
    }

    @Override
    public List<Product> getPersisted() throws PersistenceException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            List<Product> products = new ArrayList<>();
            String name;
            while ((name = reader.readLine()) != null) {            //czytamy po linijce ponieważ dane produktów zapisane są w oddzielnych liniach
                int count = Integer.parseInt(reader.readLine());    //w kolejnosci nazwa produktu, ilość, cena
                BigDecimal price = BigDecimal.valueOf(Double.parseDouble(reader.readLine()));
                products.add(
                        Product.builder()
                                .name(name)
                                .quantity(count)
                                .price(price)
                                .build()
                );
            }
            return products;
        } catch (IOException e) {
            throw new PersistenceException(e.getMessage());
        }
    }
}
