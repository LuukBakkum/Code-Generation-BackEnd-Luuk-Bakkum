package nl.inholland.codegeneration.models.DTO.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ATMRequestDTO(
        @Schema(example = "NL06INHO0000000001")
        @NotNull(message = "IBAN cannot be null!")
        @NotEmpty(message = "IBAN cannot be empty!")
        @Size(min = 18, max = 18, message = "Invalid IBAN provided!")
        String accountIban,
        @Schema(example = "20")
        @NotNull(message = "Amount cannot be null!")
        @Min(value = 0, message = "Amount cannot be lower than zero!")
        @Max(value = Integer.MAX_VALUE, message = "Amount is too high!")
        BigDecimal amount
) {
    public ATMRequestDTO(String accountIban, BigDecimal amount) {
        this.accountIban = accountIban;
        this.amount = amount;
    }
}