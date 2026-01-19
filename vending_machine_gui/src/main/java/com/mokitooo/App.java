package com.mokitooo;

import com.mokitooo.config.AppConfig;
import com.mokitooo.gui.ProgramWindow;
import com.mokitooo.persistance.product.FileProductPersistence;
import com.mokitooo.repository.product.InMemoryProductRepository;
import com.mokitooo.service.product.ProductServiceImpl;

public class App {
    public static void main(String[] args) {
        new ProgramWindow(
                new ProductServiceImpl(new InMemoryProductRepository(), new FileProductPersistence()),
                new ProductServiceImpl(new InMemoryProductRepository(), new FileProductPersistence()),
                new FileProductPersistence()
        );
    }
}