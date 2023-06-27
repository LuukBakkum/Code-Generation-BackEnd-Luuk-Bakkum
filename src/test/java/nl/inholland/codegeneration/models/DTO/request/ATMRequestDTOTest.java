package nl.inholland.codegeneration.models.DTO.request;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ATMRequestDTOTest {
    @Test
    public void testConstructor_setsFieldsCorrectly() {
        // Given
        String accountIban = "NL88INHO0001204817";
        BigDecimal amount = new BigDecimal(10);

        // When
        ATMRequestDTO dto = new ATMRequestDTO(accountIban, amount);

        // Then
        assertEquals(dto.accountIban(), accountIban);
        assertEquals(dto.amount(), amount);
    }
}
