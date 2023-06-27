package nl.inholland.codegeneration.services;

import nl.inholland.codegeneration.exceptions.APIException;
import nl.inholland.codegeneration.models.DTO.request.UserUpdateRequestDTO;
import nl.inholland.codegeneration.models.DTO.response.UserResponseDTO;
import nl.inholland.codegeneration.models.QueryParams;
import nl.inholland.codegeneration.models.Role;
import nl.inholland.codegeneration.models.User;
import nl.inholland.codegeneration.models.DTO.request.UserRequestDTO;
import nl.inholland.codegeneration.repositories.AccountRepository;
import nl.inholland.codegeneration.repositories.TransactionRepository;
import nl.inholland.codegeneration.repositories.UserRepository;
import nl.inholland.codegeneration.services.mappers.AccountDTOMapper;
import nl.inholland.codegeneration.services.mappers.UserDTOMapper;

import org.springframework.data.domain.PageImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import java.lang.reflect.Field;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    AccountRepository accountRepository;
    @Mock
    UserDTOMapper userDTOMapper;
    @InjectMocks
    UserService userService;
    @Mock
    private AccountDTOMapper accountDTOMapper;
    @Mock
    PasswordEncoder passwordEncoder;

    User authenticationUser = new User(null, null, null, null, null, null, null, null, null, null, null, null);

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        // UserDTOMapper userDTOMapper = Mockito.mock(UserDTOMapper.class);

        userDTOMapper = new UserDTOMapper(Mockito.mock(TransactionRepository.class));
        userDTOMapper.toUser = Mockito.mock(Function.class);
        userDTOMapper.toResponseDTO = Mockito.mock(Function.class);


        userService = new UserService(userRepository, accountRepository, userDTOMapper, passwordEncoder);

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

    // TODO: Add tests for the getAll method
    @Test
    void testGetAll() throws Exception {

        // QueryParams<?> queryParams = new QueryParams<>(String.class, null, null);

        QueryParams<User> queryParams = new QueryParams<>();
        // Get the classReference field
        Field field = User.class.getDeclaredField("username");
        // Allow the field to be accessed, even though it's private
        field.setAccessible(true);

        // Change the value of classReference
        Class<?> newClassReference = Integer.class;
        field.set(queryParams, newClassReference);

        // Check that classReference was changed
        Class<?> actualClassReference = (Class<?>) field.get(queryParams);
        assertEquals(newClassReference, actualClassReference);
        
        // Arrange

        queryParams.setFilter("username:'johndoe'");
        Boolean hasAccount = true;
        List<User> userList = new ArrayList<>();
        User user1 = new User();
        user1.setUsername("johndoe");
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("janedoe");
        userList.add(user1);
        userList.add(user2);
        Specification<User> specification = Specification.where(null);
        when(userRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(userList));

        // Act
        List<UserResponseDTO> userResponseDTOList = userService.getAll(queryParams, hasAccount);

        // Assert
        assertEquals(2, userResponseDTOList.size());
        assertEquals("John", userResponseDTOList.get(0).firstName());
        assertEquals("Jane", userResponseDTOList.get(1).firstName());
    }

    @Test
    public void testGetById_whenUserExists() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        userService.getById(1L);
        verify(userRepository).findById(1L);
    }

    @Test
    public void testGetById_whenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        try {
            userService.getById(1L);
        } catch (EntityNotFoundException e) {
            // Expected exception
        }
    }

    @Test
    public void testAddUser() {
        UserRequestDTO userRequestDTO = new UserRequestDTO(List.of(1), "username", "password", "firstname", "lastname",
                "email@example.com", "1234567890", LocalDate.now(), new BigDecimal("1000"), new BigDecimal("200"));
        // ... set other fields as needed
        User user = new User();
        when(userDTOMapper.toUser.apply(userRequestDTO)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        userService.add(userRequestDTO);
        verify(userRepository).save(user);
    }

    @Test
    public void testUpdateUser() {
        UserUpdateRequestDTO userUpdateRequestDTO = new UserUpdateRequestDTO(1L, List.of(1), "username", "password",
                "firstname", "lastname",
                "email@example.com", "1234567890", LocalDate.now(), new BigDecimal("1000"), new BigDecimal("200"));
        // ... set other fields as needed
        User user = new User();
        user.setId(1L);

        user.setPassword("");
        when(userDTOMapper.toUser.apply(userUpdateRequestDTO)).thenReturn(user);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        userService.update(userUpdateRequestDTO, 1L);
        verify(userRepository).save(user);
    }

    @Test
    public void testUpdateUser_invalidId() {
        UserUpdateRequestDTO userUpdateRequestDTO = new UserUpdateRequestDTO(1L, List.of(1), "username", "password",
                "firstname", "lastname",
                "email@example.com", "1234567890", LocalDate.now(), new BigDecimal("1000"), new BigDecimal("200"));
        User user = new User();
        user.setId(1L);

        user.setPassword("");
        when(userDTOMapper.toUser.apply(userUpdateRequestDTO)).thenReturn(user);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        EntityNotFoundException  exception = assertThrows(EntityNotFoundException.class, () ->  userService.update(userUpdateRequestDTO, 1L));

        assertEquals("User not found!", exception.getMessage());
    }

    @Test
    public void testDeleteUser() throws APIException {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        userService.delete(1L);
        verify(userRepository).save(user);
    }
}