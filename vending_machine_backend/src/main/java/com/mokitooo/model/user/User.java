package com.mokitooo.model.user;

import lombok.Getter;
import lombok.NonNull;

import java.math.BigDecimal;

@Getter
public class User {
    private BigDecimal balance;

    public User(BigDecimal balance) {
        setBalance(balance);
    }

    public void setBalance(@NonNull BigDecimal balance) {
        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Balance cannot be negative");
        }
        this.balance = balance;
    }

    public boolean canAfford(BigDecimal price) {
        return balance.compareTo(price) >= 0;
    }
}
