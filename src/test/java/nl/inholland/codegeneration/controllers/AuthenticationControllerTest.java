package nl.inholland.codegeneration.controllers;

import nl.inholland.codegeneration.configuration.apiTestConfiguration;
import nl.inholland.codegeneration.security.requests.AuthenticationRequest;
import nl.inholland.codegeneration.security.requests.RegisterRequest;
import nl.inholland.codegeneration.security.response.AuthenticationResponse;
import nl.inholland.codegeneration.services.AuthenticateService;
// import nl.inholland.codegeneration.services.TransactionService;
// import nl.inholland.codegeneration.services.UserService;
// import nl.inholland.codegeneration.services.AccountService;
import nl.inholland.codegeneration.services.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import io.cucumber.messages.internal.com.fasterxml.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AuthenticationController.class)
@Import(apiTestConfiguration.class)
public class AuthenticationControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticateService authenticateService;

    private String token;

    @BeforeEach
    void setUp() {
        // TODO: Replace this with actual logic to generate or get the token
        // this.token = "Bearer " + jwtService.createToken("Your User details");

        // If the token is static, you can directly assign it like this:
        // this.token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huZG9lIiwiaWF0IjoxNjg0Nzg0OTU5LCJleHAiOjE2ODQ4MjA5NTl9.Tcrz5wvxcAVmgudWcbVjbiDlMM2mRJSvvBjQDQEWp-Q";
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testRegister() throws Exception {
        // Given
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("testUser");
        registerRequest.setPassword("testPassword");
        AuthenticationResponse expectedResponse = new AuthenticationResponse("dummy_token");
    
        when(authenticateService.register(any(RegisterRequest.class))).thenReturn(expectedResponse);
    
        // When & Then
        mockMvc.perform(post("/authenticate/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt").value("dummy_token"));
    }
    
    @Test
    void testLogin() throws Exception {
        // Given
        AuthenticationRequest loginRequest = new AuthenticationRequest();
        loginRequest.setUsername("testUser");
        loginRequest.setPassword("testPassword");
        AuthenticationResponse expectedResponse = new AuthenticationResponse("dummy_token");
    
        when(authenticateService.login(any(AuthenticationRequest.class))).thenReturn(expectedResponse);
    
        // When & Then
        mockMvc.perform(post("/authenticate/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt").value("dummy_token"));
    } 
}