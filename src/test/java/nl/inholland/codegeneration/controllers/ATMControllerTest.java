package nl.inholland.codegeneration.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import nl.inholland.codegeneration.exceptions.APIExceptionHandler;
import nl.inholland.codegeneration.models.AccountType;
import nl.inholland.codegeneration.models.DTO.request.ATMRequestDTO;
import nl.inholland.codegeneration.models.DTO.request.AccountRequestDTO;
import nl.inholland.codegeneration.models.DTO.response.AccountResponseDTO;
import nl.inholland.codegeneration.models.DTO.response.TransactionResponseDTO;
import nl.inholland.codegeneration.models.Role;
import nl.inholland.codegeneration.models.User;
import nl.inholland.codegeneration.services.ATMService;
import nl.inholland.codegeneration.services.AccountService;
import nl.inholland.codegeneration.services.AuthenticateService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ATMControllerTest {
    @InjectMocks
    private ATMController atmController;

    @Mock
    private ATMService atmService;

    private MockMvc mockMvc;

    User authenticationUser = new User(null, null, null, null, null, null, null, null, null, null, null, null);

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        atmController = new ATMController(atmService);
        mockMvc = MockMvcBuilders.standaloneSetup(atmController)
                .setControllerAdvice(new APIExceptionHandler())
                .build();

        authenticationUser.setUsername("sarawilson");
        authenticationUser.setPassword("sara123");
        authenticationUser.setRoles(Collections.singletonList(Role.EMPLOYEE)); // Assuming the user has the role
        // "EMPLOYEE"

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                authenticationUser,
                "sara123",
                authenticationUser.getAuthorities());

        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);
    }

    @Test
    @WithMockUser(username = "user", roles = {"EMPLOYEE"})
    public void testDeposit() throws Exception {
        ATMRequestDTO requestDTO = new ATMRequestDTO("NL88INHO0001204817", new BigDecimal(10));
        TransactionResponseDTO responseDTO = new TransactionResponseDTO(1L, LocalDateTime.now(), "NL06INHO0000000001", "meinbank", "NL88INHO0001204817", "sarawilson", new BigDecimal(10), "Mock ATM deposit at Mein Bank HQ");

        when(atmService.deposit(requestDTO)).thenReturn(responseDTO);
        mockMvc.perform(post("/atm/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountFromIban", Matchers.is(responseDTO.accountFromIban())))
                .andExpect(jsonPath("$.accountToIban", Matchers.is(responseDTO.accountToIban())));
    }

    @Test
    @WithMockUser(username = "user", roles = {"EMPLOYEE"})
    public void testInvalidDeposit() throws Exception {
        ATMRequestDTO requestDTO = new ATMRequestDTO("NL88INHO0001204817", new BigDecimal(-10));
//        TransactionResponseDTO responseDTO = new TransactionResponseDTO(1l, LocalDateTime.now(), "NL06INHO0000000001", "meinbank", "NL88INHO0001204817", "sarawilson", new BigDecimal(10), "Mock ATM deposit at Mein Bank HQ");

        mockMvc.perform(post("/atm/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(result -> assertEquals(List.of("Amount cannot be lower than zero!").toString(),
                        ((MethodArgumentNotValidException) Objects.requireNonNull(result.getResolvedException())).getBindingResult().getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList().toString()));
    }

    @Test
    @WithMockUser(username = "user", roles = {"EMPLOYEE"})
    public void testWithdraw() throws Exception {
        ATMRequestDTO requestDTO = new ATMRequestDTO("NL88INHO0001204817", new BigDecimal(10));
        TransactionResponseDTO responseDTO = new TransactionResponseDTO(1L, LocalDateTime.now(), "NL88INHO0001204817", "sarawilson", "NL06INHO0000000001", "meinbank", new BigDecimal(10), "Mock ATM withdrawal at Mein Bank HQ");

        when(atmService.withdraw(requestDTO)).thenReturn(responseDTO);
        mockMvc.perform(post("/atm/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountFromIban", Matchers.is(responseDTO.accountFromIban())))
                .andExpect(jsonPath("$.accountToIban", Matchers.is(responseDTO.accountToIban())));
    }

    @Test
    @WithMockUser(username = "user", roles = {"EMPLOYEE"})
    public void testInvalidWithdraw() throws Exception {
        ATMRequestDTO requestDTO = new ATMRequestDTO("NL88INHO0001204817", new BigDecimal(-10));
//        TransactionResponseDTO responseDTO = new TransactionResponseDTO(1l, LocalDateTime.now(), "NL06INHO0000000001", "meinbank", "NL88INHO0001204817", "sarawilson", new BigDecimal(10), "Mock ATM deposit at Mein Bank HQ");

        mockMvc.perform(post("/atm/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(result -> assertEquals(List.of("Amount cannot be lower than zero!").toString(),
                        ((MethodArgumentNotValidException) Objects.requireNonNull(result.getResolvedException())).getBindingResult().getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList().toString()));
    }

    public static String asJsonString(final Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
