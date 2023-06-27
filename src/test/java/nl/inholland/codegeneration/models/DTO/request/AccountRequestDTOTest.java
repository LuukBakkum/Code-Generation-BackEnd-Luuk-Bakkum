package nl.inholland.codegeneration.models.DTO.request;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

public class AccountRequestDTOTest {

    @Test
    public void testConstructor_whenAbsoluteLimitIsNull_thenSetsItToZero() {
        // Given
        Long customerId = 1L;
        BigDecimal absoluteLimit = null;
        int accountType = 1;

        // When
        AccountRequestDTO dto = new AccountRequestDTO(customerId, absoluteLimit, accountType);

        // Then
        assertEquals(dto.customerId(), customerId);
        assertEquals(dto.absoluteLimit(), BigDecimal.ZERO);
        assertEquals(dto.accountType(), accountType);
    }

    @Test
    public void testConstructor_whenAbsoluteLimitIsProvided_thenSetsItCorrectly() {
        // Given
        Long customerId = 1L;
        BigDecimal absoluteLimit = new BigDecimal("1000");
        int accountType = 1;

        // When
        AccountRequestDTO dto = new AccountRequestDTO(customerId, absoluteLimit, accountType);

        // Then
        assertEquals(dto.customerId(), customerId);
        assertEquals(dto.absoluteLimit(), absoluteLimit);
        assertEquals(dto.accountType(), accountType);
    }
}
