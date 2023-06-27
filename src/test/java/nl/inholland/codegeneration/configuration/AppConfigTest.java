package nl.inholland.codegeneration.configuration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import nl.inholland.codegeneration.models.User;
import nl.inholland.codegeneration.repositories.UserRepository;

@ExtendWith(MockitoExtension.class)
public class AppConfigTest {
  @Mock
  UserRepository userRepository;
  
  @Mock
  AuthenticationConfiguration authenticationConfiguration;

  @Mock
  AuthenticationManager authenticationManager;

  @Mock
  PasswordEncoder passwordEncoder;

  @Mock
  UserDetailsService userDetailsService;

  @Mock
  UserRepository mockUserRepository;

  @Mock
  UserDetails userDetails;

  @Test
  void testPasswordEncoder() {

    AppConfig appConfig = new AppConfig(userRepository);
    PasswordEncoder passwordEncoder = appConfig.PasswordEncoder();
    assertNotNull(passwordEncoder);
  }

  @Test
  void testAuthenticationManager() throws Exception {
    when(authenticationConfiguration.getAuthenticationManager()).thenReturn(authenticationManager);

    AppConfig appConfig = new AppConfig(mock(UserRepository.class));
    AuthenticationManager result = appConfig.authenticationManager(authenticationConfiguration);

    assertNotNull(result);
  }

  @Test
  void testAuthenticationProvider() {


    AppConfig appConfig = new AppConfig(mockUserRepository);
    AuthenticationProvider authenticationProvider = appConfig.authenticationProvider();
    assertNotNull(authenticationProvider);
  }

  @Test
  void testUserDetailsService() {
    when(userRepository.findByUsername("test")).thenReturn(Optional.of(new User()));

    AppConfig appConfig = new AppConfig(userRepository);
    UserDetailsService userDetailsService = appConfig.userDetailsService();
    UserDetails userDetails = userDetailsService.loadUserByUsername("test");

    assertNotNull(userDetails);

  }
}
