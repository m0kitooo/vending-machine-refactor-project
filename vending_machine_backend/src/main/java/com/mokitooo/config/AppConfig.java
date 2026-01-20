package com.mokitooo.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static lombok.AccessLevel.PRIVATE;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public class AppConfig {
    private final int productContainerCapacity;
    private final String productdataFilepath;

    public static final AppConfig INSTANCE;

    static {
        Properties properties = new Properties();
        try (InputStream input = AppConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not load application.properties", e);
        }
        int capacity = Integer.parseInt(properties.getProperty("product.container.capacity", "20"));
        String filepath = properties.getProperty("productdata.filepath", "TableData.txt");
        INSTANCE = new AppConfig(capacity, filepath);
    }
}
