package nl.inholland.codegeneration.security.requests;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.inholland.codegeneration.models.MinAge;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @Schema(example = "testUser")
    @NotNull(message = "Username cannot be null!")
    @NotEmpty(message = "Username cannot be empty!")
    @Size(min = 5, message = "Username is too short!")
    @Size(max = 25, message = "Username is too long!")
    private String username;

    @Schema(example = "test123")
    @NotNull(message = "Password cannot be null!")
    @NotEmpty(message = "Password cannot be empty!")
    @Size(min = 5, message = "Password is too short!")
    @Size(max = 80, message = "Password is too long!")
    private String password;

    @Schema(example = "Test")
    @NotNull(message = "First name cannot be null!")
    @NotEmpty(message = "First name cannot be empty!")
    @Size(max = 25, message = "First name is too long!")
    private String firstName;

    @Schema(example = "User")
    @NotNull(message = "Last name cannot be null!")
    @NotEmpty(message = "Last name cannot be empty!")
    @Size(max = 25, message = "Last name is too long!")
    private String lastName;

    @Schema(example = "test@user.dev")
    @NotNull(message = "Email cannot be null!")
    @NotEmpty(message = "Email cannot be empty!")
    @Email(message = "Invalid email address!")
    @Size(max = 120, message = "Email is too long!")
    private String email;

    @Schema(example = "06 12345678")
    @NotNull(message = "Phone number cannot be null!")
    @NotEmpty(message = "Phone number cannot be empty!")
    @Size(max = 30, message = "Username is too long!")
    private String phoneNumber;

    @Schema(example = "2001-01-01")
    @NotNull(message = "Birthdate cannot be null!")
    @MinAge(18)
    private LocalDate birthdate;
}
