package nl.inholland.codegeneration.models;

import java.util.Arrays;

public enum AccountType {
    CURRENT(0),
    SAVINGS(1);

    private final int value;

    AccountType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static AccountType fromInt(int value) {
        return Arrays.stream(AccountType.values()).filter(accountType -> accountType.getValue() == value)
                .findFirst().orElseThrow(() -> new IllegalArgumentException("No such Account Type!"));
    }
}
