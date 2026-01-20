package com.mokitooo.gui.component;

import com.mokitooo.gui.UserModeScreen;
import com.mokitooo.mapper.ProductMapper;
import com.mokitooo.model.product.dto.ProductDTO;
import com.mokitooo.model.user.User;
import com.mokitooo.service.product.ProductService;

import javax.swing.*;
import java.util.List;

public class SaveButton extends JButton {
    public SaveButton(
            User user,
            ProductService productService,
            UserModeScreen userModeScreen,
            List<ProductDTO> productsToUpdate
    ) {
        super("Zapisz");
        setSize(150, 25);
        setFocusable(false);

        addActionListener(e -> {
            userModeScreen.getUser().setBalance(user.getBalance());   //ustawienie pieniędzy na drugiej podstronie, aby można było tam kupywać za nie produkty
            userModeScreen.moneyLabelUpdateUI();

            productsToUpdate.clear();
            productsToUpdate.addAll(productService.findAll());

            userModeScreen.tableContainerUpdateUI();                        //aktualizacja tego graficznie oraz dodanie ActionListenera dla przycisków "Kup"

            try {
                ProductMapper productMapper = new ProductMapper();
                productService.persistProducts(productsToUpdate.stream().map(productMapper::toDto).toList());                       //zapisanie produktów automatu do pliku
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Błąd zapisu.");
            }
        });
    }
}
