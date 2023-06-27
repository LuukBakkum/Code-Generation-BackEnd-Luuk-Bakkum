package nl.inholland.codegeneration.controllers;

import jakarta.persistence.EntityNotFoundException;
import nl.inholland.codegeneration.configuration.apiTestConfiguration;
import nl.inholland.codegeneration.exceptions.APIExceptionHandler;
import nl.inholland.codegeneration.models.*;
import nl.inholland.codegeneration.models.DTO.response.BalanceResponseDTO;
import nl.inholland.codegeneration.models.DTO.response.TransactionResponseDTO;
import nl.inholland.codegeneration.models.DTO.request.AccountRequestDTO;
import nl.inholland.codegeneration.models.DTO.response.AccountResponseDTO;
import nl.inholland.codegeneration.services.AccountService;
import nl.inholland.codegeneration.services.AuthenticateService;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AuthenticationController.class)
@Import(apiTestConfiguration.class)
public class AccountControllerTest {

    @InjectMocks
    private AccountController accountController;

    @Mock
    private AccountService accountService;

    @MockBean
    private AuthenticateService authenticateService;

    private MockMvc mockMvc;

    List<AccountResponseDTO> mockAccounts;

    List<TransactionResponseDTO> mockTransactions;

    User authenticationUser = new User(null, null, null, null, null, null, null, null, null, null, null, null);

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        accountController = new AccountController(accountService);
        mockMvc = MockMvcBuilders.standaloneSetup(accountController)
                .setControllerAdvice(new APIExceptionHandler())
                .build();

        this.mockAccounts = List.of(
            new AccountResponseDTO("NL06INHO0000000001", AccountType.CURRENT.getValue(), "testUser1", new BigDecimal(10), BigDecimal.ZERO),
            new AccountResponseDTO("NL07INHO0000000002", AccountType.CURRENT.getValue(), "testUser2", new BigDecimal(20), BigDecimal.ZERO),
            new AccountResponseDTO("NL08INHO0000000003", AccountType.CURRENT.getValue(), "testUser3", new BigDecimal(30), BigDecimal.ZERO),
            new AccountResponseDTO("NL09INHO0000000004", AccountType.CURRENT.getValue(), "testUser4", new BigDecimal(40), BigDecimal.ZERO),
            new AccountResponseDTO("NL10INHO0000000005", AccountType.CURRENT.getValue(), "testUser5", new BigDecimal(50), BigDecimal.ZERO)
        );

        this.mockTransactions = List.of(
                new TransactionResponseDTO(1L, LocalDateTime.now(), "NL06INHO0000000001", "testUser1", "NL07INHO0000000002", "testUser2", new BigDecimal(20), "first test description"),
                new TransactionResponseDTO(2L, LocalDateTime.now(), "NL07INHO0000000002", "testUser2", "NL08INHO0000000003", "testUser3", new BigDecimal(20), "second test description"),
                new TransactionResponseDTO(3L, LocalDateTime.now(), "NL08INHO0000000003", "testUser3", "NL09INHO0000000004", "testUser4", new BigDecimal(20), "third test description"),
                new TransactionResponseDTO(4L, LocalDateTime.now(), "NL09INHO0000000004", "testUser4", "NL10INHO0000000005", "testUser5", new BigDecimal(20), "fourth test description"),
                new TransactionResponseDTO(5L, LocalDateTime.now(), "NL10INHO0000000005", "testUser5", "NL11INHO0000000006", "testUser6", new BigDecimal(20), "fifth test description")
        );

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
    public void testGetAll() throws Exception {
        when(accountService.getAll(any(QueryParams.class))).thenReturn(mockAccounts);
        mockMvc.perform(get("/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(mockAccounts.size())));
    }

    @Test
    @WithMockUser(username = "user", roles = {"EMPLOYEE"})
    public void testGetAllWithFilter() throws Exception {
        String filter = URLEncoder.encode("iban:'NL06INHO0000000001',", StandardCharsets.UTF_8);

        List<AccountResponseDTO> filteredAccounts = mockAccounts.stream()
                .filter(account -> Objects.equals(account.iban(), "NL06INHO0000000001"))
                .toList();

        when(accountService.getAll(any(QueryParams.class))).thenAnswer(invocation -> {
            if(invocation.getArgument(0) instanceof QueryParams) {
                return filteredAccounts;
            } else {
                return mockAccounts;
            }
        });

        mockMvc.perform(get("/accounts")
            .param("filter", filter))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(filteredAccounts.size())))
            .andExpect(jsonPath("$[0].iban", Matchers.is(filteredAccounts.get(0).iban())));
    }

    @Test
    @WithMockUser(username = "user", roles = {"EMPLOYEE"})
    public void testGetAllWithLimit() throws Exception {
        int limit = 2;

        List<AccountResponseDTO> filteredAccounts = mockAccounts.subList(0, limit);

        when(accountService.getAll(any(QueryParams.class))).thenAnswer(invocation -> {
            if(invocation.getArgument(0) instanceof QueryParams && ((QueryParams<?>) invocation.getArgument(0)).getLimit() == limit) {
                return filteredAccounts;
            } else {
                return mockAccounts;
            }
        });

        mockMvc.perform(get("/accounts")
            .param("limit", Integer.toString(limit)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(filteredAccounts.size())))
            .andExpect(jsonPath("$[0].iban", Matchers.is(filteredAccounts.get(0).iban())))
            .andExpect(jsonPath("$[1].iban", Matchers.is(filteredAccounts.get(1).iban())));

    }

    @Test
    @WithMockUser(username = "user", roles = {"EMPLOYEE"})
    public void testGetAllWithPagination() throws Exception {
        int limit = 2;
        int page = 1;

        List<AccountResponseDTO> filteredAccounts = mockAccounts.subList((page * limit), ((page * limit) + limit));

        when(accountService.getAll(any(QueryParams.class))).thenAnswer(invocation -> {
            if(invocation.getArgument(0) instanceof QueryParams && ((QueryParams<?>) invocation.getArgument(0)).getLimit() == limit && ((QueryParams<?>) invocation.getArgument(0)).getPage() == page) {
                return filteredAccounts;
            } else {
                return mockAccounts;
            }
        });

        mockMvc.perform(get("/accounts")
            .param("limit", Integer.toString(limit))
            .param("page", Integer.toString(page)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(filteredAccounts.size())))
            .andExpect(jsonPath("$[0].iban", Matchers.is(mockAccounts.get(2).iban())))
            .andExpect(jsonPath("$[1].iban", Matchers.is(mockAccounts.get(3).iban())));
    }

    @Test
    @WithMockUser(username = "user", roles = {"EMPLOYEE"})
    public void testGetAllWithFilterAndLimitAndPagination() throws Exception {
        String filter = URLEncoder.encode("balance>:'30',", StandardCharsets.UTF_8);
        int limit = 2;
        int page = 1;

        List<AccountResponseDTO> filteredAccounts = mockAccounts.stream()
                .filter(account -> BigDecimal.TEN.compareTo(account.balance()) < 0)
                .toList().subList((page * limit), ((page * limit) + limit));

        when(accountService.getAll(any(QueryParams.class))).thenAnswer(invocation -> {
            if(invocation.getArgument(0) instanceof QueryParams && ((QueryParams<?>) invocation.getArgument(0)).getLimit() == limit && ((QueryParams<?>) invocation.getArgument(0)).getPage() == page) {
                return filteredAccounts;
            } else {
                return mockAccounts;
            }
        });

        mockMvc.perform(get("/accounts")
            .param("limit", Integer.toString(limit))
            .param("page", Integer.toString(page)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(filteredAccounts.size())))
            .andExpect(jsonPath("$[0].iban", Matchers.is(mockAccounts.get(3).iban())))
            .andExpect(jsonPath("$[1].iban", Matchers.is(mockAccounts.get(4).iban())));
    }

    @Test
    @WithMockUser(username = "user", roles = { "EMPLOYEE" })
    public void testGetAccountByIban() throws Exception {
        String iban = "NL06INHO0000000001";
        when(accountService.getAccountByIban(iban)).thenReturn(mockAccounts.get(0));
        mockMvc.perform(get("/accounts/{iban}", iban))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.iban", Matchers.is(iban)));
    }

    @Test
    @WithMockUser(username = "user", roles = { "EMPLOYEE" })
    public void testGetAccountByInvalidIban() throws Exception {
        String invalidIban = "NL01INHO0000000001";
        when(accountService.getAccountByIban(invalidIban)).thenThrow(new EntityNotFoundException("Account not found!"));
        mockMvc.perform(get("/accounts/{iban}", invalidIban))
            .andExpect(status().isNotFound())
            .andExpect(result -> assertTrue(result.getResolvedException() instanceof EntityNotFoundException))
            .andExpect(result -> assertEquals("Account not found!", Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    @WithMockUser(username = "user", roles = { "EMPLOYEE" })
    public void testInsertAccount() throws Exception {
        AccountRequestDTO requestDTO = new AccountRequestDTO(1L, new BigDecimal(10), AccountType.CURRENT.getValue());

        when(accountService.insertAccount(requestDTO)).thenReturn(mockAccounts.get(0));
        mockMvc.perform(post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.iban", Matchers.is(mockAccounts.get(0).iban())));
    }

    @Test
    @WithMockUser(username = "user", roles = { "EMPLOYEE" })
    public void testInsertInvalidAccount() throws Exception {
        AccountRequestDTO invalidRequestDTO = new AccountRequestDTO(null, new BigDecimal(10), AccountType.CURRENT.getValue());

        mockMvc.perform(post("/accounts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(invalidRequestDTO)))
            .andExpect(status().isBadRequest())
            .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
            .andExpect(result -> assertEquals(List.of("Customer id cannot be null!").toString(),
                    ((MethodArgumentNotValidException) Objects.requireNonNull(result.getResolvedException())).getBindingResult().getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList().toString()));
    }

    @Test
    @WithMockUser(username = "user", roles = { "EMPLOYEE" })
    void testUpdateAccount() throws Exception {
        AccountRequestDTO requestDTO = new AccountRequestDTO(1L, new BigDecimal("100"), AccountType.CURRENT.getValue());
        AccountResponseDTO responseDTO = new AccountResponseDTO("NL06INHO0000000001", AccountType.CURRENT.getValue(), "testUser1", new BigDecimal("100"), BigDecimal.ZERO);

        when(accountService.updateAccount(requestDTO, "NL06INHO0000000001")).thenReturn(responseDTO);
        mockMvc.perform(put("/accounts/{iban}", "NL06INHO0000000001")
            .content(asJsonString(requestDTO))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.iban", Matchers.is(responseDTO.iban())))
            .andExpect(jsonPath("$.absoluteLimit", Matchers.is(Integer.valueOf(responseDTO.absoluteLimit().toString()))));
    }

    @Test
    @WithMockUser(username = "user", roles = { "EMPLOYEE" })
    void testUpdateInvalidAccount() throws Exception {
        AccountRequestDTO requestDTO = new AccountRequestDTO(1L, new BigDecimal("100"), -1);
//        AccountResponseDTO responseDTO = new AccountResponseDTO("NL06INHO0000000001", AccountType.CURRENT.getValue(), "testUser1", new BigDecimal("100"), BigDecimal.ZERO);

        mockMvc.perform(put("/accounts/{iban}", "NL06INHO0000000001")
            .content(asJsonString(requestDTO))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
            .andExpect(result -> assertEquals(List.of("Account type cannot be lower than zero!").toString(),
                    ((MethodArgumentNotValidException) Objects.requireNonNull(result.getResolvedException())).getBindingResult().getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList().toString()));
    }

    @Test
    @WithMockUser(username = "user", roles = { "EMPLOYEE" })
    public void testDeleteAccount() throws Exception {
        mockMvc.perform(delete("/accounts/{id}", "NL06INHO0000000001")).andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user", roles = { "EMPLOYEE" })
    public void testDeleteInvalidAccount() throws Exception {
        String invalidIban = "NL01INHO0000000001";

        doThrow(new EntityNotFoundException("Account not found!")).when(accountService).deleteAccount(invalidIban);
        mockMvc.perform(delete("/accounts/{id}", invalidIban))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof EntityNotFoundException))
                .andExpect(result -> assertEquals("Account not found!", Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    @WithMockUser(username = "user", roles = {"CUSTOMER"})
    public void testGetTransactions() throws Exception {
        String iban = "NL06INHO0000000001";
        when(accountService.getTransactions(any(QueryParams.class), eq(iban))).thenReturn(mockTransactions);
        mockMvc.perform(get("/accounts/{id}/transactions", iban))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(mockTransactions.size())));
    }

    @Test
    @WithMockUser(username = "user", roles = {"CUSTOMER"})
    public void testInvalidGetTransactions() throws Exception {
        String invalidIban = "NL01INHO0000000001";
        when(accountService.getTransactions(any(QueryParams.class), eq(invalidIban))).thenThrow(new EntityNotFoundException("Account not found!"));
        mockMvc.perform(get("/accounts/{id}/transactions", invalidIban))
            .andExpect(status().isNotFound())
            .andExpect(result -> assertTrue(result.getResolvedException() instanceof EntityNotFoundException))
            .andExpect(result -> assertEquals("Account not found!", Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    @WithMockUser(username = "user", roles = {"CUSTOMER"})
    public void testGetBalance() throws Exception {
        String iban = "NL06INHO0000000001";
        when(accountService.getBalance(iban)).thenReturn(new BalanceResponseDTO(mockAccounts.get(0).balance()));
        mockMvc.perform(get("/accounts/{id}/balance", iban))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.balance", Matchers.is(Integer.valueOf(mockAccounts.get(0).balance().toString()))));
    }

    @Test
    @WithMockUser(username = "user", roles = {"CUSTOMER"})
    public void testInvalidGetBalance() throws Exception {
        String invalidIban = "NL01INHO0000000001";
        when(accountService.getBalance(invalidIban)).thenThrow(new EntityNotFoundException("Account not found!"));
        mockMvc.perform(get("/accounts/{id}/balance", invalidIban))
            .andExpect(status().isNotFound())
            .andExpect(result -> assertTrue(result.getResolvedException() instanceof EntityNotFoundException))
            .andExpect(result -> assertEquals("Account not found!", Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    public static String asJsonString(final Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule()); // register JavaTimeModule Dit geeft meer errors. Moet iets
                                                         // extra toevoegen.
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}