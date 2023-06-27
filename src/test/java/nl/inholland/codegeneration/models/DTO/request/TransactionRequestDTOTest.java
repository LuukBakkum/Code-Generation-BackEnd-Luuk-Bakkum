package nl.inholland.codegeneration.models.DTO.request;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

public class TransactionRequestDTOTest {

    @Test
    public void testConstructor_whenDescriptionIsNull_thenSetsItToEmptyString() {
        // Given
        String accountFromIban = "NL91 ABNA 0417 1643 00";
        String accountToIban = "NL91 ABNA 0417 1643 01";
        BigDecimal amount = new BigDecimal("100");
        String description = null;

        // When
        TransactionRequestDTO dto = new TransactionRequestDTO(accountFromIban, accountToIban, amount, description);

        // Then
        assertEquals(dto.accountFromIban(), accountFromIban);
        assertEquals(dto.accountToIban(), accountToIban);
        assertEquals(dto.amount(), amount);
        assertEquals(dto.description(), "");
    }

    @Test
    public void testConstructor_whenDescriptionIsProvided_thenSetsItCorrectly() {
        // Given
        String accountFromIban = "NL91 ABNA 0417 1643 00";
        String accountToIban = "NL91 ABNA 0417 1643 01";
        BigDecimal amount = new BigDecimal("100");
        String description = "Test Transaction";

        // When
        TransactionRequestDTO dto = new TransactionRequestDTO(accountFromIban, accountToIban, amount, description);

        // Then
        assertEquals(dto.accountFromIban(), accountFromIban);
        assertEquals(dto.accountToIban(), accountToIban);
        assertEquals(dto.amount(), amount);
        assertEquals(dto.description(), description);
    }
}