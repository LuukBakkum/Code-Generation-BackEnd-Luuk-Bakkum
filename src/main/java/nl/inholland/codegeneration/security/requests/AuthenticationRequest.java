package nl.inholland.codegeneration.security.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {
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
}
