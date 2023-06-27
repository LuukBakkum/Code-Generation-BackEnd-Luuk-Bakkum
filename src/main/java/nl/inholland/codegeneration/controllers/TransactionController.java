package nl.inholland.codegeneration.controllers;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nl.inholland.codegeneration.models.DTO.request.TransactionRequestDTO;
import nl.inholland.codegeneration.models.DTO.response.APIExceptionResponseDTO;
import nl.inholland.codegeneration.models.DTO.response.TransactionResponseDTO;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import nl.inholland.codegeneration.models.QueryParams;
import nl.inholland.codegeneration.models.Transaction;
import nl.inholland.codegeneration.services.TransactionService;

@RestController
@RequestMapping(path = "/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Transactions endpoints")
@SecurityRequirement(name = "bearerToken")
public class TransactionController {

    private final TransactionService transactionService;

    @Parameter(in = ParameterIn.QUERY, name ="filter", description = "Filter in the following format &lt;field&gt;&lt;operator&gt;'&lt;value&gt;' separated by a comma. Example: accountFrom.user.firstName:'John',accountTo.user.id&gt;:'3'", schema = @Schema(type = "string"))
    @Parameter(in = ParameterIn.QUERY, name ="limit", description = "The number of users that are to be returned (default: 12).", schema = @Schema(type = "int"))
    @Parameter(in = ParameterIn.QUERY, name ="page", description = "The page number (default: 0).", schema = @Schema(type = "int"))
    @Operation(summary = "Get all transactions", description = "Get all transactions", tags = { "Transactions", "Employee", "Customer" } )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = TransactionResponseDTO.class)))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Bad Request!\",\"httpStatus\": \"BAD_REQUEST\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Unauthorized!\",\"httpStatus\": \"UNAUTHORIZED\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Forbidden!\",\"httpStatus\": \"FORBIDDEN\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Internal Server Error!\",\"httpStatus\": \"INTERNAL_SERVER_ERROR\",\"timestamp\": \"2001-01-01T00:00:00\"}")))
    })
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAll(@RequestParam(value = "filter", required = false) String filterQuery,
                                    @RequestParam(value = "limit", required = false) Integer limit,
                                    @RequestParam(value = "page", required = false) Integer page) throws Exception {
        QueryParams<Transaction> queryParams = new QueryParams<>(Transaction.class, limit, page);
        queryParams.setFilter(filterQuery);
        List<TransactionResponseDTO> transactions = transactionService.getAll(queryParams);
        return ResponseEntity.status(200).body(transactions);
    }

    @Parameter(in = ParameterIn.PATH, required = true, name ="id", description = "The id of the transaction you want to get", schema = @Schema(type = "int"))
    @Operation(summary = "Get transaction by id", description = "Get transaction by id", tags = { "Transactions", "Employee" } )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TransactionResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Bad Request!\",\"httpStatus\": \"BAD_REQUEST\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Unauthorized!\",\"httpStatus\": \"UNAUTHORIZED\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Forbidden!\",\"httpStatus\": \"FORBIDDEN\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Not Found!\",\"httpStatus\": \"NOT_FOUND\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Internal Server Error!\",\"httpStatus\": \"INTERNAL_SERVER_ERROR\",\"timestamp\": \"2001-01-01T00:00:00\"}")))
    })
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getById(@PathVariable Long id) {
        TransactionResponseDTO transaction = transactionService.getById(id);
        return ResponseEntity.status(200).body(transaction);
    }

    @Operation(summary = "Add a transactions", description = "Add a transaction", tags = { "Transactions", "Employee", "Customer" } )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TransactionResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Bad Request!\",\"httpStatus\": \"BAD_REQUEST\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Unauthorized!\",\"httpStatus\": \"UNAUTHORIZED\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Forbidden!\",\"httpStatus\": \"FORBIDDEN\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Not Found!\",\"httpStatus\": \"NOT_FOUND\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Internal Server Error!\",\"httpStatus\": \"INTERNAL_SERVER_ERROR\",\"timestamp\": \"2001-01-01T00:00:00\"}")))
    })
    @PreAuthorize("hasAuthority('EMPLOYEE') OR hasAuthority('CUSTOMER')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> add(@RequestBody @Valid TransactionRequestDTO transactionRequestDTO) {
        TransactionResponseDTO addedTransaction = transactionService.add(transactionRequestDTO);
        return ResponseEntity.status(201).body(addedTransaction);
    }
}
