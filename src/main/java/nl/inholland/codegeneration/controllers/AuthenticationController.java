package nl.inholland.codegeneration.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import nl.inholland.codegeneration.exceptions.APIException;
import nl.inholland.codegeneration.models.DTO.response.APIExceptionResponseDTO;
import nl.inholland.codegeneration.models.DTO.response.TransactionResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import nl.inholland.codegeneration.security.requests.AuthenticationRequest;
import nl.inholland.codegeneration.security.requests.RegisterRequest;
import nl.inholland.codegeneration.security.response.AuthenticationResponse;
import nl.inholland.codegeneration.services.AuthenticateService;

@RestController
@RequestMapping(path = "/authenticate")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication endpoints")
public class AuthenticationController {
    private final AuthenticateService authenticateService;

    @Operation(summary = "Register a new user", description = "Register a new user", tags = { "Authentication", "Unauthorized" } )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TransactionResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Bad Request!\",\"httpStatus\": \"BAD_REQUEST\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Unauthorized!\",\"httpStatus\": \"UNAUTHORIZED\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Not Found!\",\"httpStatus\": \"NOT_FOUND\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Internal Server Error!\",\"httpStatus\": \"INTERNAL_SERVER_ERROR\",\"timestamp\": \"2001-01-01T00:00:00\"}")))
    })
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@Valid @RequestBody RegisterRequest request) throws APIException {
        return ResponseEntity.status(201).body(authenticateService.register(request));
    }

    @Operation(summary = "Login", description = "Login", tags = { "Authentication", "Unauthorized" } )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TransactionResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Bad Request!\",\"httpStatus\": \"BAD_REQUEST\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Unauthorized!\",\"httpStatus\": \"UNAUTHORIZED\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Not Found!\",\"httpStatus\": \"NOT_FOUND\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Internal Server Error!\",\"httpStatus\": \"INTERNAL_SERVER_ERROR\",\"timestamp\": \"2001-01-01T00:00:00\"}")))
    })
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authenticateService.login(request));
    }

}
