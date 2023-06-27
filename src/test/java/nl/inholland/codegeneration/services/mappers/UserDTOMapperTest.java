package nl.inholland.codegeneration.services.mappers;

import nl.inholland.codegeneration.models.DTO.request.UserRequestDTO;
import nl.inholland.codegeneration.models.DTO.request.UserUpdateRequestDTO;
import nl.inholland.codegeneration.models.DTO.response.UserResponseDTO;
import nl.inholland.codegeneration.models.Role;
import nl.inholland.codegeneration.models.User;
import nl.inholland.codegeneration.repositories.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class UserDTOMapperTest {

    @Mock
    private TransactionRepository transactionRepository;

    private UserDTOMapper userDTOMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userDTOMapper = new UserDTOMapper(transactionRepository);
    }

    @Test
    public void testToUser() {
        UserRequestDTO dto = new UserRequestDTO(Collections.singletonList(1), "testUser", "testPassword", "Test", "User", "test@example.com", "1234567890",
                LocalDate.now(), new BigDecimal("1000"), new BigDecimal("200"));

        User user = userDTOMapper.toUser.apply(dto);

        assertEquals(Role.fromInt(dto.roles().get(0)), user.getRoles().get(0));
        assertEquals(dto.username(), user.getUsername());
        assertEquals(dto.password(), user.getPassword());
        assertEquals(dto.firstName(), user.getFirstName());
        assertEquals(dto.lastName(), user.getLastName());
        assertEquals(dto.email(), user.getEmail());
        assertEquals(dto.phoneNumber(), user.getPhoneNumber());
        assertEquals(dto.birthdate(), user.getBirthdate());
    }

    @Test
    public void testToUserFromUpdate() {
        UserUpdateRequestDTO dto = new UserUpdateRequestDTO(1L, List.of(Role.CUSTOMER.getValue()), "testUser", "testPassword", "Test", "User", "test@example.com", "1234567890",
                LocalDate.now(), new BigDecimal("1000"), new BigDecimal("200"));

        User user = userDTOMapper.toUser.apply(dto);

        assertEquals(Role.fromInt(dto.roles().get(0)), user.getRoles().get(0));
        assertEquals(dto.username(), user.getUsername());
        assertEquals(dto.password(), user.getPassword());
        assertEquals(dto.firstName(), user.getFirstName());
        assertEquals(dto.lastName(), user.getLastName());
        assertEquals(dto.email(), user.getEmail());
        assertEquals(dto.phoneNumber(), user.getPhoneNumber());
        assertEquals(dto.birthdate(), user.getBirthdate());
    }

    @Test
    public void testToResponseDTO() {
        User user = new User();
        user.setDayLimit(BigDecimal.TEN);
        user.setId(1L);
        user.setRoles(Collections.singletonList(Role.EMPLOYEE));

        when(transactionRepository.findDailyTransactionsValueOfUser(user.getId())).thenReturn(Optional.of(BigDecimal.ONE));

        UserResponseDTO dto = userDTOMapper.toResponseDTO.apply(user);

        assertEquals(user.getDayLimit().subtract(BigDecimal.ONE), dto.remainingDayLimit());
    }
}