package nl.inholland.codegeneration.models.DTO.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import nl.inholland.codegeneration.models.MinAge;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record UserRequestDTO (
    @NotEmpty(message = "You need to include at least one role!")
    @Size(min = 1, max = 2, message = "You need to include at least one role!")
//    @PositiveOrZero(message = "All role ID's must be zero or above!")
    List<Integer> roles,
    @Schema(example = "testUser")
    @NotNull(message = "Username cannot be null!")
    @NotEmpty(message = "Username cannot be empty!")
    @Size(min = 5, message = "Username is too short!")
    @Size(max = 25, message = "Username is too long!")
    String username,
    @Schema(example = "test123")
    @NotNull(message = "Password cannot be null!")
    @NotEmpty(message = "Password cannot be empty!")
    @Size(min = 5, message = "Password is too short!")
    @Size(max = 80, message = "Password is too long!")
    String password,
    @Schema(example = "Test")
    @NotNull(message = "First name cannot be null!")
    @NotEmpty(message = "First name cannot be empty!")
    @Size(max = 25, message = "First name is too long!")
    String firstName,
    @Schema(example = "User")
    @NotNull(message = "Last name cannot be null!")
    @NotEmpty(message = "Last name cannot be empty!")
    @Size(max = 25, message = "Last name is too long!")
    String lastName,
    @Schema(example = "test@user.dev")
    @NotNull(message = "Email cannot be null!")
    @NotEmpty(message = "Email cannot be empty!")
    @Email(message = "Invalid email address!")
    @Size(max = 120, message = "Email is too long!")
    String email,
    @Schema(example = "06 12345678")
    @NotNull(message = "Phone number cannot be null!")
    @NotEmpty(message = "Phone number cannot be empty!")
    @Size(max = 30, message = "Username is too long!")
    String phoneNumber,
    @Schema(example = "2001-01-01")
    @NotNull(message = "Birthdate cannot be null!")
    @MinAge(18)
    LocalDate birthdate,
    @NotNull(message = "Day limit cannot be null!")
    @Min(value = 0, message = "Day limit cannot be lower than zero!")
    @Max(value = Integer.MAX_VALUE, message = "Day limit is too high!")
    BigDecimal dayLimit,
    @NotNull(message = "Transaction limit cannot be null!")
    @Min(value = 0, message = "Transaction limit cannot be lower than zero!")
    @Max(value = Integer.MAX_VALUE, message = "Transaction limit is too high!")
    BigDecimal transactionLimit
) implements MappableUserRequestDTO {
    public UserRequestDTO(List<Integer> roles, String username, String password, String firstName, String lastName, String email, String phoneNumber, LocalDate birthdate, BigDecimal dayLimit, BigDecimal transactionLimit) {
        this.roles = roles;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.birthdate = birthdate;
        this.dayLimit = dayLimit;
        this.transactionLimit = transactionLimit;
    }
}
