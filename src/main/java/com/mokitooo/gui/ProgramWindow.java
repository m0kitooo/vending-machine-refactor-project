package com.mokitooo.gui;

import com.mokitooo.persistance.product.FileProductPersistence;
import com.mokitooo.service.product.ProductService;

import javax.swing.*;
import java.awt.*;

//  Klasa odpowiadająca za okno aplikacji, to tutaj dołączane są do okna (JFrame) inne elementy interfejsu graficznego takie jak
//  mp: menu

public class ProgramWindow extends JFrame {

    public ProgramWindow(
            ProductService productService,
            ProductService productService2,
            FileProductPersistence fileProductPersistence
    ) {
        super("Automat sprzedający");
        setSize(900, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);

        JPanel userModeScreen1 = new JPanel();
        UserModeScreen userModeScreen = new UserModeScreen(userModeScreen1, productService2);
        JPanel editVendingMachineScreen = new JPanel();
        new EditVendingMachineScreen(editVendingMachineScreen, userModeScreen, productService, fileProductPersistence);    //przekazujemy userModeScreen aby można było zmieniać zawartość podstrony "Tryb użytkowanika" z podstrony "Edytuj automat"

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
