package nl.inholland.codegeneration.models.DTO.response;

import io.swagger.v3.oas.annotations.media.Schema;
import nl.inholland.codegeneration.models.Account;

import java.math.BigDecimal;

public record AccountResponseDTO(
    @Schema(example = "NL06INHO0000000001")
    String iban,
    int accountType,
    @Schema(example = "testUser")
    String username,
    BigDecimal balance,
    BigDecimal absoluteLimit
) {
    public AccountResponseDTO(Account account) {
        this(account.getIban(), account.getAccountType().getValue(), account.getUser().getUsername(), account.getBalance(), account.getAbsoluteLimit());
    }

}
