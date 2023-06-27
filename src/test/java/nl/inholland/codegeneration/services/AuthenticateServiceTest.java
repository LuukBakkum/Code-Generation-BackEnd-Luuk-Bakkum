package nl.inholland.codegeneration.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import nl.inholland.codegeneration.exceptions.APIException;
import nl.inholland.codegeneration.models.Role;
import nl.inholland.codegeneration.models.User;
import nl.inholland.codegeneration.repositories.UserRepository;
import nl.inholland.codegeneration.security.requests.AuthenticationRequest;
import nl.inholland.codegeneration.security.requests.RegisterRequest;
import nl.inholland.codegeneration.security.response.AuthenticationResponse;

@ExtendWith(MockitoExtension.class)
public class AuthenticateServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticateService authenticateService;

    @BeforeEach
    public void setup() {
        authenticateService = new AuthenticateService(userRepository, passwordEncoder, jwtService,
                authenticationManager);
    }

    @Test
    void testRegister() throws APIException {
        User savedUser = new User(1L, List.of(Role.CUSTOMER), "sarawilson", "sara123", "Sara", "Wilson",
                "sara.wilson@yahoo.com",
                "0612345678", LocalDate.of(1990, 11, 13), new BigDecimal(1000), new BigDecimal(200),
                false);
        RegisterRequest registerRequest = new RegisterRequest(savedUser.getUsername(), savedUser.getPassword(), savedUser.getFirstName(), savedUser.getLastName(), savedUser.getEmail(), savedUser.getPhoneNumber(), savedUser.getBirthdate());

        

        String jwtToken = "generatedToken";

        // Mock dependencies
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation->{
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });
        when(jwtService.generateToken(any())).thenReturn(jwtToken);

        // Execute the method
        AuthenticationResponse response = authenticateService.register(registerRequest);

        // Verify the result
        assertNotNull(response);
        assertEquals(jwtToken, response.getToken());
        assertEquals(savedUser.getUsername(), response.getUsername());
        assertEquals(savedUser.getEmail(), response.getEmail());
        assertEquals(List.of(Role.CUSTOMER.getValue()), response.getRoles());
        assertEquals(savedUser.getId(), response.getId());

        // Verify the method calls
        verify(passwordEncoder).encode(registerRequest.getPassword());
        verify(userRepository).save(any(User.class));
        verify(jwtService).generateToken(any(User.class));

       // verify(jwtService).generateToken(savedUser);
    }


    
    @Test
    void testLogin() {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest("user", "pass");
    
        User savedUser = new User(1L, List.of(Role.CUSTOMER), authenticationRequest.getUsername(), "sara123", "Sara", "Wilson",
                "sara.wilson@yahoo.com",
                "0612345678", LocalDate.of(1990, 11, 13), new BigDecimal(1000), new BigDecimal(200),
                false);
    
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(savedUser));
        when(jwtService.generateToken(any(User.class))).thenReturn("token");
    
        AuthenticationResponse response = authenticateService.login(authenticationRequest);
    
        assertEquals("token", response.getToken());
        assertEquals(savedUser.getUsername(), response.getUsername());
        assertEquals(savedUser.getEmail(), response.getEmail());
        assertEquals(List.of(Role.CUSTOMER.getValue()), response.getRoles());
        assertEquals(savedUser.getId(), response.getId());
    }
}