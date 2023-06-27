package nl.inholland.codegeneration.models.DTO.response;

import java.math.BigDecimal;

import nl.inholland.codegeneration.models.Account;

public record BalanceResponseDTO(BigDecimal balance) {
    public BalanceResponseDTO(Account account) {
        this(account.getBalance());
    }
}
