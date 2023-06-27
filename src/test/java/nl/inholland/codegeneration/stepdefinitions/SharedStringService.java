package nl.inholland.codegeneration.stepdefinitions;

import org.springframework.stereotype.Component;

@Component
public class SharedStringService {
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
