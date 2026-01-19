package com.mokitooo.config;

import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class AppConfig {
    @Getter
    private static final AppConfig INSTANCE = new AppConfig();

    public final int MAX_PRODUCT_CONTAINER_CAPACITY = 20;
    public final String PRODUCTDATA_FILEPATH = "TableData.txt";
}
