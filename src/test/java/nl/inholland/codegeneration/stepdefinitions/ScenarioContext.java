package nl.inholland.codegeneration.stepdefinitions;

import org.springframework.stereotype.Component;
import org.springframework.http.ResponseEntity;

@Component
public class ScenarioContext {
    private ResponseEntity<String> response;

    public void setResponse(ResponseEntity<String> response) {
        this.response = response;
    }

    public ResponseEntity<String> getResponse() {
        return this.response;
    }
}
