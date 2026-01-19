package com.mokitooo.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public class AppConfig {
    private final int productContainerCapacity;
    private final String productdataFilepath;
    
    public static final AppConfig INSTANCE = new AppConfig(
            Integer.parseInt(System.getProperty("product.container.capacity", "20")),
            System.getProperty("productdata.filepath", "TableData.txt")
    );
}
