package com.mokitooo.gui;

import com.mokitooo.gui.component.AddBalanceButton;
import com.mokitooo.gui.component.SaveButton;
import com.mokitooo.gui.component.UserBalanceField;
import com.mokitooo.gui.component.UserBalanceLabel;
import com.mokitooo.mapper.ProductMapper;
import com.mokitooo.model.product.dto.CreateProductDTO;
import com.mokitooo.model.product.dto.ProductDTO;
import com.mokitooo.persistance.product.FileProductPersistence;
import com.mokitooo.exception.ContainerFulfilledException;
import com.mokitooo.model.user.User;
import com.mokitooo.model.product.Product;
import com.mokitooo.service.product.ProductService;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static javax.swing.SwingConstants.CENTER;

public class EditVendingMachineScreen {
    private final JLabel headerLabel = new JLabel("Edytor automatu", CENTER);
    private final UserBalanceField userBalanceField = new UserBalanceField();
    private final User user = new User(BigDecimal.ZERO);
    private final UserBalanceLabel userBalanceLabel = new UserBalanceLabel(user);
    private final JButton addMoneyButton = new AddBalanceButton(userBalanceLabel, userBalanceField, user);
    private final JPanel moneyLabelPanel = new JPanel();                               //ustawienie koloru tła dla "moneyLabel"
    private final JPanel productsDataContainer = new JPanel();                         //zastosowanie graficzne, ustawienie koloru tła
    private final JLabel productLabel = new JLabel("produkt", CENTER);
    private final JLabel productCountLabel = new JLabel("ilość", CENTER);
    private final JLabel productPriceLabel = new JLabel("cena", CENTER);
    private final JTextField productField = new JTextField();
    private final JTextField productCountField = new JTextField();
    private final JTextField productPriceField = new JTextField();
    private final JButton addProductButton = new JButton("Dodaj produkt");
    private final JPanel tableContainer = new JPanel();    //ważna zmienna tutaj znajdują się graficzne odwzorowania produktów
    private final ProductService productService;

    //przycisk służący do wczytania ostatnio zapisanego automatu
    private final JButton lastVendingMachineButton = new JButton("<html><div style='text-align: center;'>Ostatnio zapisany<br>automat</div></html>");
    private final JButton saveButton;
    private final ProductMapper productMapper = new ProductMapper();

    public EditVendingMachineScreen(
            JPanel jPanel,
            UserModeScreen userModeScreen,
            ProductService productService,
            FileProductPersistence fileProductPersistence
    ) {
        this.productService = productService;
        this.saveButton = new SaveButton(user, productService, fileProductPersistence, userModeScreen);

        jPanel.setLayout(null);
        styleScreen();
        addComponents(jPanel);
        placeComponents();
        addActions();
    }

    //podpina do odpowiednich komponentów inne komponenty
    private final void addComponents(JPanel jPanel) {
        jPanel.add(headerLabel);
        jPanel.add(lastVendingMachineButton);
        jPanel.add(userBalanceField);
        jPanel.add(addMoneyButton);
        moneyLabelPanel.add(userBalanceLabel);    //dodane do panelu aby mozna bylo ustawic tlo
        jPanel.add(moneyLabelPanel);
        productsDataContainer.add(productLabel);
        productsDataContainer.add(productCountLabel);
        productsDataContainer.add(productPriceLabel);
        productsDataContainer.add(saveButton);
        productsDataContainer.add(productField);
        productsDataContainer.add(productCountField);
        productsDataContainer.add(productPriceField);
        productsDataContainer.add(addProductButton);
        jPanel.add(productsDataContainer);
        jPanel.add(tableContainer);
    }

    //odpowiada za ułożenie komponentów w odpowiednich miejscach
    private void placeComponents() {
        headerLabel.setBounds(350, 0, 200, 25);
        lastVendingMachineButton.setBounds(15, 50, 120, 50);

        userBalanceField.setLocation(150, 25);
        addMoneyButton.setLocation(350, 25);
        moneyLabelPanel.setBounds(550, 25, 200, 25);

        productsDataContainer.setBounds(150, 50, 600, 50);
        productLabel.setBounds(0, 0, 150, 25);
        productCountLabel.setBounds(150, 0, 150, 25);
        productPriceLabel.setBounds(300, 0, 150, 25);
        saveButton.setLocation(450, 0);
        productField.setBounds(0, 25, 150, 25);
        productCountField.setBounds(150, 25, 150, 25);
        productPriceField.setBounds(300, 25, 150, 25);
        addProductButton.setBounds(450, 25, 150, 25);

        tableContainer.setBounds(150, 100, 600, 525);
    }

    //stylizacja, odpwiada za np: kolor tła w JPanel-u itp.
    private void styleScreen() {
        productsDataContainer.setLayout(null);
        tableContainer.setLayout(null);
        moneyLabelPanel.setBackground(Color.lightGray);
        addProductButton.setFocusable(false);
        productsDataContainer.setBackground(Color.gray);
        tableContainer.setBackground(Color.lightGray);
        lastVendingMachineButton.setFocusable(false);
    }

    private void addActions() {
        addProductButton.addActionListener(evt -> {
            try {
                try {
                    productService.register(CreateProductDTO.builder()
                            .name(productField.getText())
                            .quantity(Integer.parseInt(productCountField.getText()))
                            .price(BigDecimal.valueOf(Double.parseDouble(productPriceField.getText())))
                            .build()
                    );
                    tableContainerUpdateUI();                       //wizualna zmiana dla użytkownika, żeby widział dodany produkt
                } catch (ContainerFulfilledException e) {
                    JOptionPane.showMessageDialog(null, "Nie można dodać więcej produktów.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Wprowadź dane poprawnie, pamiętaj muszisz wypełnić wszystkie pola.");
            } finally {
                productField.setText("");
                productCountField.setText("");
                productPriceField.setText("");
            }
        });

        //  wczytanie ostatnio zapisanego automatu
        lastVendingMachineButton.addActionListener(evt -> {
//            try {
//                ArrayList<Product> products = fileProductPersistence.readProductsFromFile();
//                productService.deleteAll(productService.findAll());
//                productService.saveAll(products);
                productService.reloadData();
                tableContainerUpdateUI();
//            } catch (IOException e) {
//                JOptionPane.showMessageDialog(null, "Błąd odczytu pliku.");
//            } catch (NumberFormatException e) {
//                JOptionPane.showMessageDialog(null, "Błąd formatu danych w pliku.");
//            } catch (IndexOutOfBoundsException e) {
//                JOptionPane.showMessageDialog(null, "Niekompletne dane w pliku.");
//            }
        });
    }

    //graficzne wylistowanie produktów dodanych do automatu i dodanie obok nich przycisku do ich usuwania (funkcja nie dodaje tej funkcjonalności tylko ustawia dla nich ActionListener-a)
    private void tableContainerUpdateUI() {
        List<ProductDTO> products = productService.findAll();
        tableContainer.removeAll();
        int y = 0;
        for (ProductDTO product : products) {
            JLabel nameLabel = new JLabel(product.name(), CENTER);
            JLabel countLabel = new JLabel(String.valueOf(product.quantity()), CENTER);
            JLabel priceLabel = new JLabel(String.format("%.2f", product.price()), CENTER);
            JButton jButton = new JButton("Usuń");
            jButton.setFocusable(false);
            jButton.addActionListener(e -> {
                productService.deleteById(product.id());
                tableContainerUpdateUI();
            });
            nameLabel.setBounds(0, y, 150, 25);
            countLabel.setBounds(150, y, 150, 25);
            priceLabel.setBounds(300, y, 150, 25);
            jButton.setBounds(450, y, 150, 25);
            tableContainer.add(nameLabel);
            tableContainer.add(countLabel);
            tableContainer.add(priceLabel);
            tableContainer.add(jButton);
            y += 25;
        }
        tableContainer.revalidate();
        tableContainer.repaint();
    }
}
