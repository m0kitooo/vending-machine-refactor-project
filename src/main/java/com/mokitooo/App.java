package com.mokitooo;

import com.mokitooo.config.AppConfig;
import com.mokitooo.gui.ProgramWindow;
import com.mokitooo.persistance.product.FileProductPersistence;
import com.mokitooo.repository.product.InMemoryProductRepository;
import com.mokitooo.service.product.ProductServiceImpl;

public class App {
    public static void main(String[] args) {
        int storageSize = AppConfig.getINSTANCE().MAX_PRODUCT_CONTAINER_CAPACITY;
        new ProgramWindow(
                new ProductServiceImpl(new InMemoryProductRepository(), storageSize),
                new ProductServiceImpl(new InMemoryProductRepository(), storageSize),
                new FileProductPersistence(AppConfig.getINSTANCE().PRODUCTDATA_FILEPATH)
        );
    }
}