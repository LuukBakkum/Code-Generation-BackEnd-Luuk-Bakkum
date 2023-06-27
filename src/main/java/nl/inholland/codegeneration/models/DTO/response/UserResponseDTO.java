package nl.inholland.codegeneration.models.DTO.response;

import io.swagger.v3.oas.annotations.media.Schema;
import nl.inholland.codegeneration.models.Role;
import nl.inholland.codegeneration.models.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public record UserResponseDTO(
    @Schema(example = "1")
    Long id,
    List<Integer> roles,
    @Schema(example = "testUser")
    String username,
    @Schema(example = "Test")
    String firstName,
    @Schema(example = "User")
    String lastName,
    @Schema(example = "test@user.dev")
    String email,
    @Schema(example = "06 12345678")
    String phoneNumber,
    @Schema(example = "2001-01-01")
    LocalDate birthdate,
    BigDecimal dayLimit,
    BigDecimal remainingDayLimit,
    BigDecimal transactionLimit
) {
    public UserResponseDTO(User user, BigDecimal remainingDayLimit) {
        this(user.getId(), user.getRoles().stream().map(Role::getValue).collect(Collectors.toList()), user.getUsername(), user.getFirstName(), user.getLastName(),
                user.getEmail(), user.getPhoneNumber(), user.getBirthdate(), user.getDayLimit(), remainingDayLimit, user.getTransactionLimit());
    }
}
