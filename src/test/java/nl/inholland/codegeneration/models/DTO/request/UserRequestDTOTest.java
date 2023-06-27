package nl.inholland.codegeneration.models.DTO.request;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

public class UserRequestDTOTest {

    @Test
    public void testConstructor_setsFieldsCorrectly() {
        // Given
        List<Integer> roles = new ArrayList<>();
        roles.add(1);
        String username = "testUser";
        String password = "testPassword";
        String firstName = "John";
        String lastName = "Doe";
        String email = "john.doe@test.com";
        String phoneNumber = "1234567890";
        LocalDate birthdate = LocalDate.now();
        BigDecimal dailyLimit = new BigDecimal("1000");
        BigDecimal transactionLimit = new BigDecimal("200");

        // When
        UserRequestDTO dto = new UserRequestDTO(roles, username, password, firstName, lastName, email, phoneNumber, birthdate, dailyLimit, transactionLimit);

        // Then
        assertEquals(dto.roles(), roles);
        assertEquals(dto.username(), username);
        assertEquals(dto.password(), password);
        assertEquals(dto.firstName(), firstName);
        assertEquals(dto.lastName(), lastName);
        assertEquals(dto.email(), email);
        assertEquals(dto.phoneNumber(), phoneNumber);
        assertEquals(dto.birthdate(), birthdate);
    }
}