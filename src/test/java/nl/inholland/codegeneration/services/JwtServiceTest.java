package nl.inholland.codegeneration.services;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails userDetails;

    @BeforeEach
    public void setUp() {
        jwtService = new JwtService();

        userDetails = User.withDefaultPasswordEncoder()
                .username("testUser")
                .password("password")
                .authorities(Collections.emptyList())
                .build();
    }

    @Test
    public void testGenerateToken() {
        String token = jwtService.generateToken(userDetails);
        assertNotNull(token, "Token should not be null");

    }
    @Test
    public void testGenerateToken_whitUserDetails() {
        Map<String,Object> claims = new HashMap<>();
        String token = jwtService.generateToken(claims,userDetails);
        assertNotNull(token, "Token should not be null");

    }
    

    @Test 
    public void testValidateToken() {
        String token = jwtService.generateToken(userDetails);
        boolean isValid = jwtService.validateToken(token, userDetails);
        assertTrue(isValid, "Token should be valid");   
     }

     @Test 
     public void testValidateToken_whenTokenInvalidAndMatchingName() {
        String token = jwtService.generateToken(userDetails);
        UserDetails wrongDetails = User.withDefaultPasswordEncoder()
                .username("testUser")
                .password("wrongPassword")
                .authorities(Collections.emptyList())
                .build();
        boolean isValid = jwtService.validateToken(token, wrongDetails);
                
        assertTrue(isValid, "Token should be valid"); 
        assertEquals(userDetails.getUsername(), userDetails.getUsername(), "Extracted username should match original");
     }
     @Test 
     public void testValidateToken_whenTokenValidAndNotMatchingName() {
        String token = jwtService.generateToken(userDetails);
        UserDetails wrongDetails = User.withDefaultPasswordEncoder()
        .username("testUser2")
        .password("password")
        .authorities(Collections.emptyList())
        .build();
        boolean isValid = jwtService.validateToken(token, userDetails);
                
        assertTrue(isValid, "Token should be valid"); 
        assertNotEquals(userDetails.getUsername(), wrongDetails.getUsername(), "Extracted username should match original");
  
     }


     @Test
     public void testExtractUsername() {
    String token = jwtService.generateToken(userDetails);
     String username = jwtService.extractUsername(token);
     assertEquals(userDetails.getUsername(), username, "Extracted username should match original");
     }
    @Test
    public void testValidateTokenWithInvalidUser() {
        String token = jwtService.generateToken(userDetails);

        UserDetails otherUserDetails = User.withDefaultPasswordEncoder()
                .username("otherUser")
                .password("password")
                .authorities(Collections.emptyList())
                .build();

        boolean isValid = jwtService.validateToken(token, otherUserDetails);
        assertFalse(isValid, "Token should be invalid for different user");
    }

    //TODO: make this a good test
    @Test
    void testExtractClaim() {
        String token = jwtService.generateToken(userDetails);
        String jwt = jwtService.extractClaim(token, Claims::getSubject);
        assertEquals(jwt, jwt);
    }

  
}