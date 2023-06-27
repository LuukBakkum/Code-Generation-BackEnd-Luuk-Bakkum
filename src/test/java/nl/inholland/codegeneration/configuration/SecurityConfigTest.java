package nl.inholland.codegeneration.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import nl.inholland.codegeneration.security.JwtFilter;

public class SecurityConfigTest {

  private SecurityConfig securityConfig;

  @Mock
  private JwtFilter jwtFilter;

  @Mock
  private AuthenticationProvider authenticationProvider;

  @Mock
  HttpSecurity http;

  @Mock
  HttpSecurityBuilder builder;
  @Mock
  SecurityFilterChain securityFilterChain;

  @BeforeEach
  public void setup() {
    securityConfig = new SecurityConfig(jwtFilter, authenticationProvider);
  }

  @Test
  void testFilterChain() {
    // Mock objects
//    when(http.csrf(Mockito.any())).thenReturn(http);
//    when(builder.disable()).thenReturn(builder);
//    when(builder.authorizeHttpRequests()).thenReturn(builder);
//    when(builder.requestMatchers(Mockito.any())).thenReturn(builder);
//    when(builder.permitAll()).thenReturn(builder);
//    when(builder.anyRequest()).thenReturn(builder);
//    when(builder.authenticated()).thenReturn(builder);
//    when(builder.cors()).thenReturn(builder);
//    when(builder.and()).thenReturn(securityFilterChain);
//    when(http.build()).thenReturn(securityFilterChain);
//
//    // Test the filterChain() method
//    securityConfig.filterChain(http);
//
//    // Verify the interactions
//    verify(http).csrf(Mockito.any());
//    verify(builder).disable();
//    verify(builder).authorizeHttpRequests();
//    verify(builder).requestMatchers(Mockito.any());
//    verify(builder).permitAll();
//    verify(builder).anyRequest();
//    verify(builder).authenticated();
//    verify(builder).cors();
//    verify(http).build();
  }

  @Test
  void testCorsConfigurationSource() {
    SecurityConfig securityConfig = new SecurityConfig(null, null);

    CorsConfigurationSource corsConfigurationSource = securityConfig.corsConfigurationSource();

    assertTrue(corsConfigurationSource instanceof UrlBasedCorsConfigurationSource);

    UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = (UrlBasedCorsConfigurationSource) corsConfigurationSource;

    CorsConfiguration registeredConfiguration = urlBasedCorsConfigurationSource.getCorsConfiguration(mock(HttpServletRequest.class));

    assertEquals(List.of("http://localhost:5173"), registeredConfiguration.getAllowedOrigins());
    assertEquals(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"), registeredConfiguration.getAllowedMethods());
    assertEquals(Arrays.asList("Authorization", "Cache-Control", "Content-Type"),
        registeredConfiguration.getAllowedHeaders());
    assertEquals(Arrays.asList("Authorization", "Cache-Control", "Content-Type"),
        registeredConfiguration.getExposedHeaders());
    assertTrue(Boolean.TRUE.equals(registeredConfiguration.getAllowCredentials()));
  }

//  @Test
//  void testUserDetailsManager() {
//    // Create an instance of SecurityConfig
//    SecurityConfig securityConfig = new SecurityConfig(null, null);
//
//
//
//    // Call the userDetailsManager() method
//    InMemoryUserDetailsManager userDetailsManager = securityConfig.userDetailsManager();
//
//    // Get the user details from the userDetailsManager
//    UserDetails user = userDetailsManager.loadUserByUsername("dev");
//
//    // Assert the properties of the user details
//    assertEquals("dev", user.getUsername());
//
//    // Compare the password using BCrypt encoding
//    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//    assertTrue(passwordEncoder.matches("dev123", user.getPassword()));
//
//    assertTrue(user.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
//  }
}
