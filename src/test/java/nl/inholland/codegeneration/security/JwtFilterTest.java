package nl.inholland.codegeneration.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nl.inholland.codegeneration.models.Role;
import nl.inholland.codegeneration.models.User;
import nl.inholland.codegeneration.services.JwtService;

@ExtendWith(MockitoExtension.class)
public class JwtFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private Authentication authentication;


    @InjectMocks
    private JwtFilter jwtFilter;

    @BeforeEach
    public void setup() {

        //  jwtService = new JwtService();
    }

    @Test
    public void testDoFilterInternal_WithValidToken_ShouldSetAuthenticationInSecurityContextHolder() throws ServletException, IOException {
    String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huZG9lIiwiaWF0IjoxNjg2NDgzNzI2LCJleHAiOjE2ODY1MTk3MjZ9.6oXgxlZBznZ1kUFWN1w5aAKlh0fX0Nu28sxA_xPAw84";
    String username = "testUser";

    User user = new User(1L, List.of(Role.CUSTOMER), username, "sara123", "Sara", "Wilson",
    "sara.wilson@yahoo.com",
    "0612345678", LocalDate.of(1990, 11, 13), new BigDecimal(1000), new BigDecimal(200),
    false);
   
    
    when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
    when(jwtService.extractUsername(jwt)).thenReturn(username);
    when(userDetailsService.loadUserByUsername(username)).thenReturn(user);
    when(jwtService.validateToken(jwt, user)).thenReturn(true);
    
    jwtFilter.doFilterInternal(request, response, filterChain);
    
    verify(filterChain, times(1)).doFilter(request, response);
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    assertNotNull(authentication);
    assertEquals(username, ((User) authentication.getPrincipal()).getUsername());
    }
    

    @Test
    public void testDoFilterInternal_WithInvalidToken_ShouldContinueFilterChain() throws ServletException, IOException {
        
        String jwt = "invalidToken";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);

    }

    @Test
    public void testDoFilterInternal_WithMissingToken_ShouldContinueFilterChain() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

   
 
}


