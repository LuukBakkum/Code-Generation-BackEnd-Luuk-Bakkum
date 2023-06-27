package nl.inholland.codegeneration.controllers;

import jakarta.persistence.EntityNotFoundException;
import nl.inholland.codegeneration.exceptions.APIExceptionHandler;
import nl.inholland.codegeneration.models.DTO.request.TransactionRequestDTO;
import nl.inholland.codegeneration.models.DTO.response.TransactionResponseDTO;
import nl.inholland.codegeneration.models.QueryParams;
import nl.inholland.codegeneration.services.TransactionService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TransactionControllerTest {

    private TransactionController transactionController;

    @Mock
    private TransactionService transactionService;

    private MockMvc mockMvc;

    List<TransactionResponseDTO> mockTransactions;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        transactionController = new TransactionController(transactionService);
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController)
                .setControllerAdvice(new APIExceptionHandler())
                .build();

        mockTransactions = List.of(
            new TransactionResponseDTO(1L, LocalDateTime.now(), "NL06INHO0000000001", "testUser1", "NL07INHO0000000002", "testUser2", new BigDecimal(20), "first test description"),
            new TransactionResponseDTO(2L, LocalDateTime.now(), "NL07INHO0000000002", "testUser2", "NL08INHO0000000003", "testUser3", new BigDecimal(20), "second test description"),
            new TransactionResponseDTO(3L, LocalDateTime.now(), "NL08INHO0000000003", "testUser3", "NL09INHO0000000004", "testUser4", new BigDecimal(20), "third test description"),
            new TransactionResponseDTO(4L, LocalDateTime.now(), "NL09INHO0000000004", "testUser4", "NL10INHO0000000005", "testUser5", new BigDecimal(20), "fourth test description"),
            new TransactionResponseDTO(5L, LocalDateTime.now(), "NL10INHO0000000005", "testUser5", "NL11INHO0000000006", "testUser6", new BigDecimal(20), "fifth test description")
        );
    }

    @Test
    @WithMockUser(username = "user", roles = {"EMPLOYEE"})
    public void testGetAll() throws Exception {
        when(transactionService.getAll(any(QueryParams.class))).thenReturn(mockTransactions);

        mockMvc.perform(get("/transactions")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(mockTransactions.size())));
    }

    @Test
    @WithMockUser(username = "user", roles = {"EMPLOYEE"})
    public void testGetAllWithFilter() throws Exception {
        String filter = URLEncoder.encode("id>'1',", StandardCharsets.UTF_8);

        List<TransactionResponseDTO> filteredTransactions = mockTransactions.stream()
                .filter(transaction -> transaction.id() > 1)
                .collect(Collectors.toList());

        when(transactionService.getAll(any(QueryParams.class))).thenAnswer(invocation -> {
            if(invocation.getArgument(0) instanceof QueryParams) {
                return filteredTransactions;
            } else {
                return mockTransactions;
            }
        });

        mockMvc.perform(get("/transactions").param("filter", filter)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(filteredTransactions.size())))
                .andExpect(jsonPath("$[0].id", Matchers.greaterThan(1)))
                .andExpect(jsonPath("$[1].id", Matchers.greaterThan(1)))
                .andExpect(jsonPath("$[2].id", Matchers.greaterThan(1)))
                .andExpect(jsonPath("$[3].id", Matchers.greaterThan(1)));
    }

    @Test
    @WithMockUser(username = "user", roles = {"EMPLOYEE"})
    public void testGetAllWithLimit() throws Exception {
        int limit = 2;

        List<TransactionResponseDTO> filteredTransactions = mockTransactions.subList(0, limit);

        when(transactionService.getAll(any(QueryParams.class))).thenAnswer(invocation -> {
            if(invocation.getArgument(0) instanceof QueryParams && ((QueryParams<?>) invocation.getArgument(0)).getLimit() == limit) {
                return filteredTransactions;
            } else {
                return mockTransactions;
            }
        });

        mockMvc.perform(get("/transactions").param("limit", Integer.toString(limit))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(limit)))
            .andExpect(jsonPath("$[0].id", Matchers.is(Integer.valueOf(filteredTransactions.get(0).id().toString()))))
            .andExpect(jsonPath("$[1].id", Matchers.is(Integer.valueOf(filteredTransactions.get(1).id().toString()))));
    }

    @Test
    @WithMockUser(username = "user", roles = {"EMPLOYEE"})
    public void testGetAllWithPagination() throws Exception {
        int limit = 2;
        int page = 1;

        List<TransactionResponseDTO> filteredTransactions = mockTransactions.subList((page * limit), ((page * limit) + limit));

        when(transactionService.getAll(any(QueryParams.class))).thenAnswer(invocation -> {
            if(invocation.getArgument(0) instanceof QueryParams && ((QueryParams<?>) invocation.getArgument(0)).getLimit() == limit && ((QueryParams<?>) invocation.getArgument(0)).getPage() == page) {
                return filteredTransactions;
            } else {
                return mockTransactions;
            }
        });

        mockMvc.perform(get("/transactions")
            .param("limit", Integer.toString(limit))
            .param("page", Integer.toString(page))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(limit)))
            .andExpect(jsonPath("$[0].id", Matchers.is(Integer.valueOf(mockTransactions.get(2).id().toString()))))
            .andExpect(jsonPath("$[1].id", Matchers.is(Integer.valueOf(mockTransactions.get(3).id().toString()))));
    }

    @Test
    @WithMockUser(username = "user", roles = {"EMPLOYEE"})
    public void testGetAllWithFilterAndLimitAndPagination() throws Exception {
        int limit = 2;
        int page = 1;
        String filter = URLEncoder.encode("id>'1',", StandardCharsets.UTF_8);

        List<TransactionResponseDTO> filteredTransactions = mockTransactions.stream()
                .filter(transaction -> transaction.id() > 1)
                .collect(Collectors.toList())
                .subList((page * limit), ((page * limit) + limit));

        when(transactionService.getAll(any(QueryParams.class))).thenAnswer(invocation -> {
            if(invocation.getArgument(0) instanceof QueryParams && ((QueryParams<?>) invocation.getArgument(0)).getLimit() == limit && ((QueryParams<?>) invocation.getArgument(0)).getPage() == page) {
                return filteredTransactions;
            } else {
                return mockTransactions;
            }
        });

        mockMvc.perform(get("/transactions")
            .param("filter", filter)
            .param("limit", Integer.toString(limit))
            .param("page", Integer.toString(page))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(limit)))
            .andExpect(jsonPath("$[0].id", Matchers.is(Integer.valueOf(mockTransactions.get(3).id().toString()))))
            .andExpect(jsonPath("$[1].id", Matchers.is(Integer.valueOf(mockTransactions.get(4).id().toString()))))
            .andExpect(jsonPath("$[0].id", Matchers.greaterThan(1)))
            .andExpect(jsonPath("$[1].id", Matchers.greaterThan(1)));
    }

    @Test
    @WithMockUser(username = "user", roles = {"EMPLOYEE"})
    public void testGetById() throws Exception {
        Long transactionId = 1L;

        when(transactionService.getById(transactionId)).thenReturn(mockTransactions.get(0));
        mockMvc.perform(get("/transactions/{id}", transactionId).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", Matchers.is(Integer.valueOf(transactionId.toString()))));
    }

    @Test
    @WithMockUser(username = "user", roles = {"EMPLOYEE"})
    public void testGetByInvalidId() throws Exception {
        Long InvalidTransactionId = -1L;

        when(transactionService.getById(InvalidTransactionId)).thenThrow(new EntityNotFoundException("Transaction not found!"));
        mockMvc.perform(get("/transactions/{id}", InvalidTransactionId).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(result -> assertTrue(result.getResolvedException() instanceof EntityNotFoundException))
            .andExpect(result -> assertEquals("Transaction not found!", Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    @WithMockUser(username = "user", roles = {"EMPLOYEE", "CUSTOMER"})
    public void testAdd() throws Exception {
        TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO("NL01INHO0000000001", "NL01INHO0000000002", new BigDecimal(100), "description");
        // Fill the object according to your needs
        mockMvc.perform(post("/transactions")
            .content(asJsonString(transactionRequestDTO))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "user", roles = {"EMPLOYEE", "CUSTOMER"})
    public void testInvalidAdd() throws Exception {
        TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO("Frans Bauer", "NL01INHO0000000002", new BigDecimal(100), "description");
        // Fill the object according to your needs
        mockMvc.perform(post("/transactions")
                        .content(asJsonString(transactionRequestDTO))
                        .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
            .andExpect(result -> assertEquals(List.of("Invalid IBAN provided!").toString(),
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

// package nl.inholland.codegeneration.controllers;

// import nl.inholland.codegeneration.configuration.apiTestConfiguration;
// import nl.inholland.codegeneration.controllers.TransactionController;
// import nl.inholland.codegeneration.models.DTO.request.TransactionRequestDTO;
// import nl.inholland.codegeneration.services.TransactionService;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.Mockito;
// import org.mockito.MockitoAnnotations;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.context.annotation.Import;
// import org.springframework.http.MediaType;
// import org.springframework.security.test.context.support.WithMockUser;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.test.web.servlet.setup.MockMvcBuilders;

// import com.fasterxml.jackson.databind.ObjectMapper;

// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// import java.math.BigDecimal;

// @SpringBootTest
// @AutoConfigureMockMvc
// @Import(apiTestConfiguration.class)
// public class TransactionControllerTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @MockBean
//     private TransactionService transactionService;

//     @MockBean
//     private TransactionController transactionController;

//     // private TransactionRequestDTO transactionRequestDTO;

//     @BeforeEach
//     public void setup() {
//         // setup before each test
//         MockitoAnnotations.initMocks(this);
//         mockMvc = MockMvcBuilders.standaloneSetup(transactionController).build();
//     }

//     @MockBean
//     TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO(
//             "accountFromIban",
//             "accountToIban",
//             new BigDecimal("1000"),
//             "description");

//     @Test
//     @WithMockUser(username = "user", roles = {"EMPLOYEE"})
//     public void getAllTransactions_Returns200() throws Exception {
//         // Setup
//         // Assuming you would setup a list of TransactionResponseDTO objects here

//         // Execute & Verify
//         mockMvc.perform(get("/transactions")
//                 .contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isOk());
//     }

//     @Test
//     @WithMockUser(username = "user", roles = {"EMPLOYEE"})
//     public void getTransactionById_Returns200() throws Exception {
//         // Setup
//         // Assuming you would setup a TransactionResponseDTO object here

//         // Execute & Verify
//         mockMvc.perform(get("/transactions/1")
//                 .contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isOk());
//     }

//     @Test
//     @WithMockUser(username = "user", roles = {"EMPLOYEE"})
//     public void addTransaction_Returns201() throws Exception {
//         // Setup
//         TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO(
//                 "accountFromIban",
//                 "accountToIban",
//                 new BigDecimal("1000"),
//                 "description");
    
//         ObjectMapper objectMapper = new ObjectMapper();
//         String transactionRequestJson = objectMapper.writeValueAsString(transactionRequestDTO);
    
//         // Execute & Verify
//         mockMvc.perform(post("/transactions")
//                 .content(transactionRequestJson)
//                 .contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isCreated());
//     }
// }