package com.mokitooo.gui;

import com.mokitooo.model.product.dto.ProductDTO;
import com.mokitooo.model.user.User;
import com.mokitooo.model.product.Product;
import com.mokitooo.service.product.ProductService;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

import static javax.swing.SwingConstants.CENTER;

//  Klasa odpowiada za interfejs graficzny oraz funkcjonalność drugiej podstrony aplikacji (tryb użytkownika)
//  tutaj użytkownik może kupywać produkty które utworzył w oknie (edytuj automat)

public class UserModeScreen {
    private final JLabel headerLabel = new JLabel("Tryb użytkownika", CENTER);
    private final JLabel moneyLabel = new JLabel("0.00$");
    private final JPanel moneyLabelContainer = new JPanel();
    private final JPanel productsTagsContainer = new JPanel();
    private final JLabel productLabel = new JLabel("produkt", CENTER);
    private final JLabel productCountLabel = new JLabel("ilość", CENTER);
    private final JLabel productPriceLabel = new JLabel("cena", CENTER);
    private final JPanel tableContainer = new JPanel();
    @Getter
    private final User user = new User(BigDecimal.ZERO);
    @Getter
    private final ProductService productService;

    public UserModeScreen(JPanel jPanel, ProductService productService) {
        this.productService = productService;
        jPanel.setLayout(null);
        productsTagsContainer.setLayout(null);
        tableContainer.setLayout(null);
        addComponents(jPanel);
        setComponentsBounds();
        styleScreen();
    }

    private void addComponents(JPanel jPanel) {
        jPanel.add(headerLabel);
        moneyLabelContainer.add(moneyLabel);
        jPanel.add(productsTagsContainer);
        productsTagsContainer.add(productLabel);
        productsTagsContainer.add(productCountLabel);
        productsTagsContainer.add(productPriceLabel);
        jPanel.add(moneyLabelContainer);
        jPanel.add(tableContainer);
    }

    private void setComponentsBounds() {
        headerLabel.setBounds(350, 0, 200, 25);
        moneyLabelContainer.setBounds(150, 25 ,600, 25);
        productLabel.setBounds(0, 0, 150, 25);
        productCountLabel.setBounds(150, 0, 150, 25);
        productPriceLabel.setBounds(300, 0, 150, 25);
        productsTagsContainer.setBounds(150, 50, 600, 25);
        tableContainer.setBounds(150, 75, 600, 525);
    }

    //funkcja odpowiada za graficzne zmiany Komponentów, tutaj zmainy kolorów
    private void styleScreen() {
        moneyLabelContainer.setBackground(Color.lightGray);
        productsTagsContainer.setBackground(Color.gray);
        tableContainer.setBackground(Color.lightGray);
    }

    public void moneyLabelUpdateUI() {
        moneyLabel.setText(String.format("%.2f$", user.getBalance()));
        moneyLabel.revalidate();
        moneyLabel.repaint();
    }

    //graficzne wylistowanie produktów dodanych do automatu i dodanie obok nich przycisku do ich zakupu (funkcja nie dodaje tej funkcjonalności tylko ustawia dla nich ActionListener-a)
    //robi to samo co ta sama funkcja w klasia "EditVendingMachineScreen" tylko dodaje przycisku "Kup"
    public void tableContainerUpdateUI() {
//        List<Product> products = productManager.getProducts();
        List<ProductDTO> products = productService.findAll();
        tableContainer.removeAll();
        int y = 0;
        for (ProductDTO product : products) {
            JLabel nameLabel = new JLabel(product.name(), CENTER);
            JLabel countLabel = new JLabel(String.valueOf(product.quantity()), CENTER);
            JLabel priceLabel = new JLabel(String.format("%.2f", product.price()), CENTER);
            JButton jButton = new JButton("Kup");
            jButton.setFocusable(false);
            jButton.addActionListener(e -> {
                buyProduct(product);
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

    private void buyProduct(ProductDTO product) {
        if (product.quantity() <= 0) {
            JOptionPane.showMessageDialog(null, "Produkt się skończył.");
            return;
        }
        if (!user.canAfford(product.price())) {
            JOptionPane.showMessageDialog(null, "Zbyt mało środków, nie można kupić produktu.");
            return;
        }

        user.setBalance(user.getBalance().subtract(product.price()));  //zmienienie stanu pieniędzy użytkownika
        moneyLabelUpdateUI();                                        //graficzna zmiana pieniędzy
        productService.update(product.decreaseQuantity());
    }
}
