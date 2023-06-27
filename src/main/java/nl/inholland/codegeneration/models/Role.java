package nl.inholland.codegeneration.models;

import org.springframework.security.core.GrantedAuthority;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum Role implements GrantedAuthority{
    EMPLOYEE(0),
    CUSTOMER(1);

    private final int value;

    Role(int value) {
        this.value = value;
    }

    @JsonValue
    public int getValue() {
        return value;
    }

    @Override
    public String getAuthority() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAuthority'");
    }

    public static Role fromInt(int value) {
        return Arrays.stream(Role.values()).filter(role -> role.getValue() == value)
                .findFirst().orElseThrow(() -> new IllegalArgumentException("No such Role!"));
    }
}
