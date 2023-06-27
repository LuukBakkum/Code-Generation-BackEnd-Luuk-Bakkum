package nl.inholland.codegeneration.models.DTO.response;

import io.swagger.v3.oas.annotations.media.Schema;
import nl.inholland.codegeneration.models.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponseDTO(
    @Schema(example = "1")
    Long id,
    LocalDateTime timestamp,
    @Schema(example = "NL06INHO0000000001")
    String accountFromIban,
    @Schema(example = "testUser")
    String accountFromUsername,
    @Schema(example = "NL07INHO0000000002")
    String accountToIban,
    @Schema(example = "otherTestUser")
    String accountToUsername,
    BigDecimal amount,
    @Schema(example = "Example description")
    String description
) {
    public TransactionResponseDTO(Transaction transaction) {
        this(transaction.getId(), transaction.getTimestamp(), transaction.getAccountFrom().getIban(), transaction.getAccountFrom().getUser().getUsername(),
                transaction.getAccountTo().getIban(), transaction.getAccountTo().getUser().getUsername(), transaction.getAmount(), transaction.getDescription());
    }
}
