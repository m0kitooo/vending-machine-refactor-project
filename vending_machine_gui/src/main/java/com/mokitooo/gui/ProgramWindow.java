package com.mokitooo.gui;

import com.mokitooo.model.product.dto.ProductDTO;
import com.mokitooo.model.user.User;
import com.mokitooo.service.product.ProductService;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProgramWindow extends JFrame {

    public ProgramWindow(
            ProductService productService
    ) {
        super("Automat sprzedający");
        setSize(900, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);

        List<ProductDTO> savedProducts = new ArrayList<>();
//        User user = new User(BigDecimal.ZERO);

        JPanel userModeScreen1 = new JPanel();
        UserModeScreen userModeScreen = new UserModeScreen(userModeScreen1, savedProducts);
        JPanel editVendingMachineScreen = new JPanel();
        new EditVendingMachineScreen(editVendingMachineScreen, userModeScreen, productService, savedProducts);

        //dodanie do cardPanel-ów paneli z podstronami aby łatow można było przełączać podstrone którą aktualnie widzi użytkownik
        JPanel cardPanel = new JPanel(new CardLayout());
        cardPanel.add(editVendingMachineScreen, "EditVendingMachineScreen");
        cardPanel.add(userModeScreen1, "UserModeScreen");
        add(cardPanel);

        MenuToolbar menuToolbar = new MenuToolbar(cardPanel);

        setJMenuBar(menuToolbar.getMenuBar());
        setVisible(true);
    }
}
