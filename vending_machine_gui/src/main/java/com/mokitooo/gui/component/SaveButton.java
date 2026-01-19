package com.mokitooo.gui.component;

import com.mokitooo.gui.UserModeScreen;
import com.mokitooo.mapper.ProductMapper;
import com.mokitooo.model.product.Product;
import com.mokitooo.model.product.dto.CreateProductDTO;
import com.mokitooo.model.product.dto.ProductDTO;
import com.mokitooo.model.user.User;
import com.mokitooo.persistance.product.FileProductPersistence;
import com.mokitooo.service.product.ProductService;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;

public class SaveButton extends JButton {
    ProductMapper productMapper = new ProductMapper();

    public SaveButton(
            User user,
            ProductService productService,
            FileProductPersistence fileProductPersistence,
            UserModeScreen userModeScreen
    ) {
        super("Zapisz");
        setSize(150, 25);
        setFocusable(false);

        addActionListener(e -> {
            userModeScreen.getUser().setBalance(user.getBalance());   //ustawienie pieniędzy na drugiej podstronie, aby można było tam kupywać za nie produkty
            userModeScreen.moneyLabelUpdateUI();                            //aktualizacja graficzna zmiany pieniędzy

            //Musiałem stworzyć taką pętle, ponieważ jak się potem okazało przekazywałem referencje do tej samej listy zamiast wartości z listy
            //w ten sposub towrzone są nowe produkty o takich samych wartosciach pól listy więc nie ma takiego problemu
            ArrayList<CreateProductDTO> createProductDTOs = new ArrayList<>();
            for (ProductDTO product : productService.findAll()) {
                createProductDTOs.add(
                        CreateProductDTO.builder()
                                .name(product.name())
                                .quantity(product.quantity())
                                .price(product.price())
                                .build()
                );
            }

//            userModeScreen.getProductManager().setProducts(products);       //ustawienie produktów na drugiej podstronie, aby użytkownik mógł je tam kupować
            ProductService productServiceTemp = userModeScreen.getProductService();
            productServiceTemp.deleteAll(ProductMapper.toIds(productServiceTemp.findAll()));
            productServiceTemp.saveAll(createProductDTOs);
            userModeScreen.tableContainerUpdateUI();                        //aktualizacja tego graficznie oraz dodanie ActionListenera dla przycisków "Kup"

//            try {
                productService.persistProducts(createProductDTOs);                       //zapisanie produktów automatu do pliku
//            } catch (IOException ex) {
//                JOptionPane.showMessageDialog(null, "Błąd zapisu.");
//            }
        });
    }
}
