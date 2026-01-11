package com.mokitooo.persistance.product;

import com.mokitooo.model.product.Product;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.StringJoiner;

@RequiredArgsConstructor
public class FileProductPersistence {
    private final String filename;

    public void writeToFile(ArrayList<Product> products) throws IOException {
        @Cleanup FileWriter writer = new FileWriter(filename);

        for (Product product : products) {
            StringJoiner joiner = new StringJoiner(System.lineSeparator())
                    .add(product.getName())
                    .add(String.valueOf(product.getQuantity()))
                    .add(String.valueOf(product.getPrice()));

            writer.write(joiner + System.lineSeparator());
        }
    }

    public ArrayList<Product> readProductsFromFile() throws IOException {
        @Cleanup BufferedReader reader = new BufferedReader(new FileReader(filename));
        ArrayList<Product> products = new ArrayList<>();
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
    }
}
