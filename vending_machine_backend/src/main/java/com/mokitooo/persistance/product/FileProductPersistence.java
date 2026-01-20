package com.mokitooo.persistance.product;

import com.mokitooo.config.AppConfig;
import com.mokitooo.exception.DataParsingException;
import com.mokitooo.exception.DataAccessException;
import com.mokitooo.exception.DataStructureException;
import com.mokitooo.exception.domain.DomainDataException;
import com.mokitooo.model.product.Product;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class FileProductPersistence implements ProductPersistence {
    private final String filename = AppConfig.INSTANCE.getProductdataFilepath();

    @Override
    public void persist(Iterable<Product> products) throws DataAccessException {
        if (products == null) {
            throw new IllegalArgumentException("products can't be null");
        }

        try (FileWriter writer = new FileWriter(filename)) {
            for (Product product : products) {
                StringJoiner joiner = new StringJoiner(System.lineSeparator())
                        .add(product.getName())
                        .add(String.valueOf(product.getQuantity()))
                        .add(String.valueOf(product.getPrice()));

                writer.write(joiner + System.lineSeparator());
            }
        } catch (IOException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public List<Product> getPersisted() throws DataAccessException, DataParsingException, DataStructureException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            List<Product> products = new ArrayList<>();
            String name;
            while ((name = reader.readLine()) != null) {            //czytamy po linijce ponieważ dane produktów zapisane są w oddzielnych liniach w kolejnosci nazwa produktu, ilość, cena
                String quantityLine = reader.readLine();
                if (quantityLine == null) {
                    throw new DataStructureException("Incomplete data: Missing quantity");
                }
                int quantity = Integer.parseInt(quantityLine.trim());

                String priceLine = reader.readLine();
                if (priceLine == null) {
                    throw new DataStructureException("Incomplete data: Missing price");
                }
                BigDecimal price = new BigDecimal(priceLine.trim());

                products.add(
                        Product.builder()
                                .name(name)
                                .quantity(quantity)
                                .price(price)
                                .build()
                );
            }
            return products;
        } catch (IOException e) {
            throw new DataAccessException(e.getMessage());
        } catch (NumberFormatException | DomainDataException e) {
            throw new DataParsingException(e.getMessage());
        }
    }


}
