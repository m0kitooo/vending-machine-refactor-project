package com.mokitooo.gui.component;

import com.mokitooo.model.user.User;
import lombok.Getter;

import javax.swing.*;
import java.math.BigDecimal;

@Getter
public class AddBalanceButton extends JButton {
    private final UserBalanceLabel userBalanceLabel;

    public AddBalanceButton(UserBalanceLabel userBalanceLabel, UserBalanceField userBalanceField, User user) {
        super("Dodaj środki");
        this.userBalanceLabel = userBalanceLabel;

        setSize(200, 25);
        setFocusable(false);

        addActionListener(evt -> {
            try {
                user.setBalance(BigDecimal.valueOf(Double.parseDouble(userBalanceField.getText())));
                userBalanceLabel.rerender();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Wprowadź liczbe.");
            } finally {
                userBalanceField.setText("");
            }
        });
    }
}
