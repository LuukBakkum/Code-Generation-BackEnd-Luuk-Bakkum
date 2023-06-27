package nl.inholland.codegeneration.services;

import nl.inholland.codegeneration.exceptions.APIException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import nl.inholland.codegeneration.models.Role;
import nl.inholland.codegeneration.models.User;
import nl.inholland.codegeneration.repositories.UserRepository;
import nl.inholland.codegeneration.security.requests.AuthenticationRequest;
import nl.inholland.codegeneration.security.requests.RegisterRequest;
import nl.inholland.codegeneration.security.response.AuthenticationResponse;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticateService {

  private final UserRepository userRepository;

  private final PasswordEncoder passwordEncoder;

  private final JwtService jwtService;

  private final AuthenticationManager authenticationManager;

  public AuthenticationResponse register(RegisterRequest request) throws APIException {
    if (Period.between(request.getBirthdate(), LocalDate.now()).getYears() < 18) {
      throw new APIException("You must at least 18 years of age to make a profile!", HttpStatus.BAD_REQUEST, null);
    }
    User user = new User();
    user.setUsername(request.getUsername());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setFirstName(request.getFirstName());
    user.setLastName(request.getLastName());
    user.setEmail(request.getEmail());
    user.setPhoneNumber(request.getPhoneNumber());
    user.setBirthdate(request.getBirthdate());

    // Registering user is by default customer
    user.setRoles(List.of(Role.CUSTOMER));
    userRepository.save(user);

    String jwtToken =  jwtService.generateToken(user);
    return AuthenticationResponse.builder()
    .token(jwtToken)
    .username(user.getUsername())
    .email(user.getEmail())
    .roles(user.getRoles().stream().map(Role::getValue).collect(Collectors.toList()))
    .id(user.getId())
    .build();
  }

  public AuthenticationResponse login(AuthenticationRequest request) {
    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
    User user = userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new EntityNotFoundException("User not found!"));
    String jwtToken =  jwtService.generateToken(user);
    return AuthenticationResponse.builder()
    .token(jwtToken)
    .username(user.getUsername())
    .email(user.getEmail())
    .roles(user.getRoles().stream().map(Role::getValue).collect(Collectors.toList()))
    .id(user.getId())
    .build();
    
  }
}
