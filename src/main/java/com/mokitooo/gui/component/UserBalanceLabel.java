package com.mokitooo.gui.component;

import com.mokitooo.model.user.User;

import javax.swing.*;

public class UserBalanceLabel extends JLabel {
    private final User user;

    public UserBalanceLabel(User user) {
        super(String.format("%.2f$", user.getBalance()), SwingConstants.CENTER);
        this.user = user;
    }

    public void rerender() {
        setText(String.format("%.2f$", user.getBalance()));
        revalidate();
        repaint();
    }
}
