package nl.inholland.codegeneration.controllers;

import jakarta.persistence.EntityNotFoundException;
import nl.inholland.codegeneration.exceptions.APIExceptionHandler;
import nl.inholland.codegeneration.models.DTO.request.UserUpdateRequestDTO;
import nl.inholland.codegeneration.models.DTO.response.UserResponseDTO;
import nl.inholland.codegeneration.models.QueryParams;
import nl.inholland.codegeneration.models.Role;
import nl.inholland.codegeneration.models.User;
import nl.inholland.codegeneration.services.UserService;
import nl.inholland.codegeneration.services.AccountService;
import nl.inholland.codegeneration.models.DTO.request.UserRequestDTO;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.http.MediaType;
// import org.springframework.security.core.userdetails.User;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.springframework.web.bind.MethodArgumentNotValidException;

public class UserControllerTest {

    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private AccountService accountService;

    private MockMvc mockMvc;

    List<UserResponseDTO> mockUsers;

    User authenticationUser = new User(null, null, null, null, null, null, null, null, null, null, null, null);

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userController = new UserController(userService, accountService);
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new APIExceptionHandler())
                .build();

        this.mockUsers = List.of(
                new UserResponseDTO(1L, List.of(Role.CUSTOMER.getValue()), "bob1", "Bob", "Bob", "bob@bob.bob", "08 08080808", LocalDate.now(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO),
                new UserResponseDTO(2L, List.of(Role.CUSTOMER.getValue()), "bob2", "Bob", "Bob", "bob@bob.bob", "08 08080808", LocalDate.now(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO),
                new UserResponseDTO(3L, List.of(Role.CUSTOMER.getValue()), "bob3", "Bob", "Bob", "bob@bob.bob", "08 08080808", LocalDate.now(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO),
                new UserResponseDTO(4L, List.of(Role.CUSTOMER.getValue()), "bob4", "Bob", "Bob", "bob@bob.bob", "08 08080808", LocalDate.now(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO),
                new UserResponseDTO(5L, List.of(Role.CUSTOMER.getValue()), "bob5", "Bob", "Bob", "bob@bob.bob", "08 08080808", LocalDate.now(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO)
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
    @WithMockUser(username = "user", roles = { "EMPLOYEE" })
    public void testGetAll() throws Exception {
//        QueryParams<User> queryParams = new QueryParams<>(User.class, 12, 0);
//        queryParams.setFilter("isDeleted:'false'");

        when(userService.getAll(any(QueryParams.class), eq(null))).thenReturn(mockUsers);
        mockMvc.perform(get("/users").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(mockUsers.size())))
            .andExpect(jsonPath("$[0].username", Matchers.is("bob1")))
            .andExpect(jsonPath("$[1].username", Matchers.is("bob2")))
            .andExpect(jsonPath("$[2].username", Matchers.is("bob3")))
            .andExpect(jsonPath("$[3].username", Matchers.is("bob4")))
            .andExpect(jsonPath("$[4].username", Matchers.is("bob5")));
    }

    @Test
    @WithMockUser(username = "user", roles = { "EMPLOYEE" })
    public void testGetAllWithFilter() throws Exception {
        when(userService.getAll(any(QueryParams.class), eq(null))).thenReturn(mockUsers);
        String filter = URLEncoder.encode("isDeleted:'false'", StandardCharsets.UTF_8);
        mockMvc.perform(get("/users").contentType(MediaType.APPLICATION_JSON).queryParam("filter", filter))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(mockUsers.size())))
            .andExpect(jsonPath("$[0].username", Matchers.is("bob1")))
            .andExpect(jsonPath("$[1].username", Matchers.is("bob2")))
            .andExpect(jsonPath("$[2].username", Matchers.is("bob3")))
            .andExpect(jsonPath("$[3].username", Matchers.is("bob4")))
            .andExpect(jsonPath("$[4].username", Matchers.is("bob5")));
    }

    @Test
    @WithMockUser(username = "user", roles = { "EMPLOYEE" })
    public void testGetAllWithLimit() throws Exception {
        int limit = 3;

        when(userService.getAll(any(QueryParams.class), eq(null))).thenReturn(mockUsers.subList(0, limit));
        mockMvc.perform(get("/users").contentType(MediaType.APPLICATION_JSON).queryParam("limit", Integer.toString(limit)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(limit)))
            .andExpect(jsonPath("$[0].username", Matchers.is("bob1")))
            .andExpect(jsonPath("$[1].username", Matchers.is("bob2")))
            .andExpect(jsonPath("$[2].username", Matchers.is("bob3")));
    }

    @Test
    @WithMockUser(username = "user", roles = { "EMPLOYEE" })
    public void testGetAllWithPage() throws Exception {
        int page = 0;

        when(userService.getAll(any(QueryParams.class), eq(null))).thenReturn(mockUsers);
        mockMvc.perform(get("/users").contentType(MediaType.APPLICATION_JSON).queryParam("page", Integer.toString(page)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(mockUsers.size())))
            .andExpect(jsonPath("$[0].username", Matchers.is("bob1")))
            .andExpect(jsonPath("$[1].username", Matchers.is("bob2")))
            .andExpect(jsonPath("$[2].username", Matchers.is("bob3")))
            .andExpect(jsonPath("$[3].username", Matchers.is("bob4")))
            .andExpect(jsonPath("$[4].username", Matchers.is("bob5")));
    }

    @Test
    @WithMockUser(username = "user", roles = { "EMPLOYEE" })
    public void testGetAllWithFilterAndLimitAndPage() throws Exception {
        String filter = URLEncoder.encode("isDeleted:'false'", StandardCharsets.UTF_8);
        int limit = 2;
        int page = 1;

        List<UserResponseDTO> filteredList = mockUsers.subList((page * limit), ((page * limit) + limit));
        when(userService.getAll(any(QueryParams.class), eq(null))).thenReturn(filteredList);
        mockMvc.perform(get("/users").contentType(MediaType.APPLICATION_JSON).queryParam("filter", filter)
                .queryParam("limit", Integer.toString(limit))
                .queryParam("page", Integer.toString(page)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(filteredList.size())))
            .andExpect(jsonPath("$[0].username", Matchers.is("bob3")))
            .andExpect(jsonPath("$[1].username", Matchers.is("bob4")));
    }

    @Test
    @WithMockUser(username = "user", roles = { "EMPLOYEE" })
    public void testGetById() throws Exception {
        Long id = 1L;

        when(userService.getById(id)).thenReturn(mockUsers.get(0));
        mockMvc.perform(get("/users/{id}", id).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", Matchers.is(Integer.valueOf(id.toString()))));
    }

    @Test
    @WithMockUser(username = "user", roles = { "EMPLOYEE" })
    public void testGetByInvalidId() throws Exception {
        Long invalidId = -1L;

        when(userService.getById(invalidId)).thenThrow(new EntityNotFoundException("User not found!"));

        mockMvc.perform(get("/users/{id}", invalidId).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(result -> assertTrue(result.getResolvedException() instanceof EntityNotFoundException))
            .andExpect(result -> assertEquals("User not found!", Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    @WithMockUser(username = "user", roles = { "EMPLOYEE" })
    public void testGetAllAccountsById() throws Exception {
        mockMvc.perform(get("/users/{id}/accounts", 1).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = { "EMPLOYEE" })
    public void testGetAllAccountsByInvalidId() throws Exception {
        Long invalidId = -1L;

        when(accountService.getAllByUserId(any(QueryParams.class), eq(invalidId))).thenThrow(new EntityNotFoundException("User not found!"));

        mockMvc.perform(get("/users/{id}/accounts", invalidId).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(result -> assertTrue(result.getResolvedException() instanceof EntityNotFoundException))
            .andExpect(result -> assertEquals("User not found!", Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    @WithMockUser(username = "user", roles = { "EMPLOYEE" })
    public void testAdd() throws Exception {
        UserRequestDTO user = new UserRequestDTO(List.of(1), "username", "password", "firstname", "lastname",
                "email@example.com", "1234567890", LocalDate.of(2001, 1, 1), new BigDecimal("1000"), new BigDecimal("200"));
        mockMvc.perform(post("/users")
            .content(asJsonString(user))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "user", roles = { "EMPLOYEE" })
    public void testInvalidAdd() throws Exception {
        UserRequestDTO user = new UserRequestDTO(List.of(1), "username", "p", "firstname", "lastname",
                "email@example.com", "1234567890", LocalDate.of(2001, 1, 1), new BigDecimal("1000"), new BigDecimal("200"));

        mockMvc.perform(post("/users")
            .content(asJsonString(user))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
            .andExpect(result -> assertEquals(List.of("Password is too short!").toString(),
                    ((MethodArgumentNotValidException) Objects.requireNonNull(result.getResolvedException())).getBindingResult().getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList().toString()));
    }

    @Test
    @WithMockUser(username = "user", roles = { "EMPLOYEE" })
    public void testUpdate() throws Exception {
        UserUpdateRequestDTO user = new UserUpdateRequestDTO(1L, List.of(Role.CUSTOMER.getValue()), "username", "password", "firstname", "lastname",
                "email@example.com", "1234567890", LocalDate.of(2001, 1, 1), new BigDecimal("1000"), new BigDecimal("200"));

        mockMvc.perform(put("/users/{id}", 1)
            .content(asJsonString(user))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = { "EMPLOYEE" })
    public void testInvalidUpdate() throws Exception {
        UserUpdateRequestDTO user = new UserUpdateRequestDTO(1L, List.of(Role.CUSTOMER.getValue()), "testUser", "password", "firstname", "lastname",
                "email@example.com", "1234567890", LocalDate.of(2007, 1, 1), new BigDecimal("1000"), new BigDecimal("200"));
        mockMvc.perform(put("/users/{id}", 1)
            .content(asJsonString(user))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
            .andExpect(result -> assertEquals(List.of("User must be at least 18 years old!").toString(),
                    ((MethodArgumentNotValidException) Objects.requireNonNull(result.getResolvedException())).getBindingResult().getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList().toString()));
    }

    @Test
    @WithMockUser(username = "user", roles = { "EMPLOYEE" })
    public void testInvalidUpdateId() throws Exception {
        Long invalidId = -1L;
        UserUpdateRequestDTO user = new UserUpdateRequestDTO(1L, List.of(Role.CUSTOMER.getValue()), "username", "password", "firstname", "lastname",
                "email@example.com", "1234567890", LocalDate.of(2001, 1, 1), new BigDecimal("1000"), new BigDecimal("200"));

        when(userService.update(user, invalidId)).thenThrow(new IllegalStateException("Id in request body must match id in url!"));

        mockMvc.perform(put("/users/{id}", invalidId)
            .content(asJsonString(user))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(result -> assertTrue(result.getResolvedException() instanceof IllegalStateException))
            .andExpect(result -> assertEquals("Id in request body must match id in url!", Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    @WithMockUser(username = "user", roles = { "EMPLOYEE" })
    public void testDelete() throws Exception {
        mockMvc.perform(delete("/users/{id}", 1).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user", roles = { "EMPLOYEE" })
    public void testInvalidDelete() throws Exception {
        Long invalidId = -1L;

        doThrow(new EntityNotFoundException("User not found!")).when(userService).delete(invalidId);
        mockMvc.perform(delete("/users/{id}", invalidId).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(result -> assertTrue(result.getResolvedException() instanceof EntityNotFoundException))
            .andExpect(result -> assertEquals("User not found!", Objects.requireNonNull(result.getResolvedException()).getMessage()));
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


