package com.mokitooo.gui;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//  Klasa odpowida za menu w itnerfejsie które znajduje się w lewym górnym rogu okna programu
//  implementuje cardapanel dzieki któremu póżniej będzie łatwiej przełączać podstrony programu

@Getter
public class MenuToolbar {
    private final JMenuBar menuBar = new JMenuBar();

    public MenuToolbar(JPanel cardPanel) {
        JMenuItem item1 = new JMenuItem("Edytuj automat");
        item1.addActionListener(e -> showCard(cardPanel, "EditVendingMachineScreen"));

        JMenuItem item2 = new JMenuItem("Tryb użytkownika");
        item2.addActionListener(e -> showCard(cardPanel, "UserModeScreen"));

        JMenu menu = new JMenu("Tryby");
        menu.add(item1);
        menu.add(item2);

        menuBar.add(menu);
    }

    private void showCard(JPanel cardPanel, String cardName) {
        CardLayout cl = (CardLayout) cardPanel.getLayout();
        cl.show(cardPanel, cardName);
    }

}
