package com.mokitooo.gui.component;

import com.mokitooo.gui.UserModeScreen;
import com.mokitooo.model.product.Product;
import com.mokitooo.model.user.User;
import com.mokitooo.persistance.product.FileProductPersistence;
import com.mokitooo.service.product.ProductService;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;

public class SaveButton extends JButton {
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
            ArrayList<Product> products = new ArrayList<>();
            for (Product product : productService.findAll()) {
                products.add(
                        Product.builder()
                                .name(product.getName())
                                .quantity(product.getQuantity())
                                .price(product.getPrice())
                                .build()
                );
            }

//            userModeScreen.getProductManager().setProducts(products);       //ustawienie produktów na drugiej podstronie, aby użytkownik mógł je tam kupować
            ProductService productServiceTemp = userModeScreen.getProductService();
            productServiceTemp.deleteAll(productServiceTemp.findAll());
            productServiceTemp.saveAll(products);
            userModeScreen.tableContainerUpdateUI();                        //aktualizacja tego graficznie oraz dodanie ActionListenera dla przycisków "Kup"

            try {
                fileProductPersistence.writeToFile(products);                       //zapisanie produktów automatu do pliku
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Błąd zapisu.");
            }
        });
    }
}
