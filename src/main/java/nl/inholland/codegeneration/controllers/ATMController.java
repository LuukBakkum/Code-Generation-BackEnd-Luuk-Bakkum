package nl.inholland.codegeneration.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nl.inholland.codegeneration.exceptions.APIException;
import nl.inholland.codegeneration.models.DTO.request.ATMRequestDTO;
import nl.inholland.codegeneration.models.DTO.response.APIExceptionResponseDTO;
import nl.inholland.codegeneration.models.DTO.response.TransactionResponseDTO;
import nl.inholland.codegeneration.security.requests.AuthenticationRequest;
import nl.inholland.codegeneration.security.requests.RegisterRequest;
import nl.inholland.codegeneration.security.response.AuthenticationResponse;
import nl.inholland.codegeneration.services.ATMService;
import nl.inholland.codegeneration.services.AuthenticateService;
import nl.inholland.codegeneration.services.IBANGenerator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/atm")
@RequiredArgsConstructor
@Tag(name = "ATM", description = "ATM endpoints")
@SecurityRequirement(name = "bearerToken")
public class ATMController {
    private final ATMService atmService;

    @Operation(summary = "Make a withdrawal", description = "Make a withdrawal", tags = { "ATM", "Employee" } )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TransactionResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Bad Request!\",\"httpStatus\": \"BAD_REQUEST\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Unauthorized!\",\"httpStatus\": \"UNAUTHORIZED\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Not Found!\",\"httpStatus\": \"NOT_FOUND\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Internal Server Error!\",\"httpStatus\": \"INTERNAL_SERVER_ERROR\",\"timestamp\": \"2001-01-01T00:00:00\"}")))
    })
    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponseDTO> deposit(@Valid @RequestBody ATMRequestDTO request) {
        return ResponseEntity.status(201).body(atmService.deposit(request));
    }

    @Operation(summary = "Make a deposit", description = "Make a deposit", tags = { "ATM", "Employee" } )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TransactionResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Bad Request!\",\"httpStatus\": \"BAD_REQUEST\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Unauthorized!\",\"httpStatus\": \"UNAUTHORIZED\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Not Found!\",\"httpStatus\": \"NOT_FOUND\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Internal Server Error!\",\"httpStatus\": \"INTERNAL_SERVER_ERROR\",\"timestamp\": \"2001-01-01T00:00:00\"}")))
    })
    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponseDTO> withdraw(@Valid @RequestBody ATMRequestDTO request) {
        return ResponseEntity.status(201).body(atmService.withdraw(request));
    }
}
