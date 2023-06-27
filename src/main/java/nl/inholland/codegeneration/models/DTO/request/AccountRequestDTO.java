package nl.inholland.codegeneration.models.DTO.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

//@JsonIgnoreProperties(ignoreUnknown = true)
public record AccountRequestDTO (
    @Schema(example = "1")
    @NotNull(message = "Customer id cannot be null!")
    @Min(value = 1L, message = "Customer id must be above 0")
    @Max(value = (Long.MAX_VALUE - 1), message = "Customer id is too high")
    Long customerId,
    @NotNull(message = "Absolute limit cannot be null!")
    @Min(value = Integer.MIN_VALUE, message = "Absolute limit is too low!")
    @Max(value = Integer.MAX_VALUE, message = "Absolute limit is too high!")
    BigDecimal absoluteLimit,
    @NotNull(message = "Account type cannot be null!")
    @Min(value = 0, message = "Account type cannot be lower than zero!")
    @Max(value = (Integer.MAX_VALUE - 1), message = "Account type is too high")
    int accountType
) {
    public AccountRequestDTO(Long customerId, BigDecimal absoluteLimit, int accountType) {
        this.customerId = customerId;
        this.absoluteLimit = (absoluteLimit != null) ? absoluteLimit : new BigDecimal(0);
        this.accountType = accountType;
    }
}
