package nl.inholland.codegeneration.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import nl.inholland.codegeneration.exceptions.APIExceptionHandler;
import nl.inholland.codegeneration.models.Role;
import nl.inholland.codegeneration.security.requests.AuthenticationRequest;
import nl.inholland.codegeneration.security.requests.RegisterRequest;
import nl.inholland.codegeneration.security.response.AuthenticationResponse;
import nl.inholland.codegeneration.services.AuthenticateService;
// import nl.inholland.codegeneration.services.TransactionService;
// import nl.inholland.codegeneration.services.UserService;
// import nl.inholland.codegeneration.services.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthenticationControllerTest {

    private MockMvc mockMvc;

    private AuthenticationController authenticationController;

    @Mock
    private AuthenticateService authenticateService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authenticationController = new AuthenticationController(authenticateService);
        mockMvc = MockMvcBuilders.standaloneSetup(authenticationController)
                .setControllerAdvice(new APIExceptionHandler())
                .build();
    }

    public static String asJsonString(final Object obj) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule()); // register JavaTimeModule Dit geeft meer errors. Moet iets
            // extra toevoegen.
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @WithMockUser(username = "user", roles = {"EMPLOYEE"})
    void testRegister() throws Exception {
        // Given
        RegisterRequest registerRequest = new RegisterRequest("testUser", "testPassword", "Test", "User", "test@user.dev", "06 12345678", LocalDate.of(2001, 1, 1));
        AuthenticationResponse expectedResponse = new AuthenticationResponse(1L, "dummy_token", List.of(Role.EMPLOYEE.getValue()), "testUser", "user@email.com");
    
        when(authenticateService.register(registerRequest)).thenReturn(expectedResponse);
    
        // When & Then
        mockMvc.perform(post("/authenticate/register")
            .content(asJsonString(registerRequest))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "user", roles = {"EMPLOYEE"})
    void testInvalidRegister() throws Exception {
        // Given
        RegisterRequest registerRequest = new RegisterRequest("testUser", "p", "Test", "User", "test@user.dev", "06 12345678", LocalDate.of(2001, 1, 1));
        AuthenticationResponse expectedResponse = new AuthenticationResponse(1L, "dummy_token", List.of(Role.EMPLOYEE.getValue()), "testUser", "user@email.com");

        when(authenticateService.register(registerRequest)).thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(post("/authenticate/register")
            .content(asJsonString(registerRequest))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
            .andExpect(result -> assertEquals(List.of("Password is too short!").toString(),
                    ((MethodArgumentNotValidException) Objects.requireNonNull(result.getResolvedException())).getBindingResult().getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList().toString()));
    }
    
    @Test
    @WithMockUser(username = "user", roles = {"EMPLOYEE"})
    void testLogin() throws Exception {
        // Given
        AuthenticationRequest loginRequest = new AuthenticationRequest();
        loginRequest.setUsername("johndoe");
        loginRequest.setPassword("john123");
        AuthenticationResponse expectedResponse = new AuthenticationResponse(1L, "dummy_token", List.of(Role.EMPLOYEE.getValue()), "testUser", "user@email.com");
    
        when(authenticateService.login(loginRequest)).thenReturn(expectedResponse);
    
        // When & Then
        mockMvc.perform(post("/authenticate/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(loginRequest)))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = {"EMPLOYEE"})
    void testInvalidCredentialsLogin() throws Exception {
        // Given
        AuthenticationRequest loginRequest = new AuthenticationRequest();
        loginRequest.setUsername("johndoe");
        loginRequest.setPassword("johnonetwothree");

        when(authenticateService.login(loginRequest)).thenThrow(new BadCredentialsException("Bad credentials"));

        // When & Then
        mockMvc.perform(post("/authenticate/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(loginRequest)))
            .andExpect(status().isUnauthorized())
            .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadCredentialsException))
            .andExpect(result -> assertEquals("Bad credentials", Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }
}