package nl.inholland.codegeneration.security.response;

import java.util.List;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    @Schema(example = "1")
    private Long id;

    @Schema(example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huZG9lIiwiaWF0IjoxNjg2NTIzNzE2LCJleHAiOjE2ODY1NTk3MTZ9.zrfh-1n_1MpkcjSqZiQvXPBHSgWFpdqNwvyh-RzzCY0")
    private String token;

    @Schema(example = "1")
    List<Integer> roles;

    @Schema(example = "testUser")
    String username;

    @Schema(example = "test@dev.nl")
    String email;
}
