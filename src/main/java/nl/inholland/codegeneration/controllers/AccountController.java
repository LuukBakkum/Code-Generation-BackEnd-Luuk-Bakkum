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
import lombok.RequiredArgsConstructor;

import nl.inholland.codegeneration.models.DTO.response.APIExceptionResponseDTO;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import nl.inholland.codegeneration.exceptions.APIException;
import nl.inholland.codegeneration.models.Account;
import nl.inholland.codegeneration.models.QueryParams;
import nl.inholland.codegeneration.models.Transaction;
import nl.inholland.codegeneration.models.DTO.request.AccountRequestDTO;
import nl.inholland.codegeneration.models.DTO.response.AccountResponseDTO;
import nl.inholland.codegeneration.models.DTO.response.BalanceResponseDTO;
import nl.inholland.codegeneration.models.DTO.response.TransactionResponseDTO;
import nl.inholland.codegeneration.services.AccountService;

@RestController
@RequestMapping(path = "/accounts")
@RequiredArgsConstructor
@Tag(name = "Accounts", description = "Accounts endpoints")
@SecurityRequirement(name = "bearerToken")
public class AccountController {
    private final AccountService accountService;

    // GET /accounts
    @Parameter(in = ParameterIn.QUERY, name ="filter", description = "Filter in the following format &lt;field&gt;&lt;operator&gt;'&lt;value&gt;' separated by a comma. Example: user.firstName:'John',user.id:'1'", schema = @Schema(type = "string"))
    @Parameter(in = ParameterIn.QUERY, name ="limit", description = "The number of users that are to be returned (default: 12).", schema = @Schema(type = "int"))
    @Parameter(in = ParameterIn.QUERY, name ="page", description = "The page number (default: 0).", schema = @Schema(type = "int"))
    @Operation(summary = "Get all accounts", description = "Get all accounts", tags = { "Accounts", "Employee" } )
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
                                    @RequestParam(value = "page", required = false) Integer page)
            throws Exception {
        QueryParams<Account> queryParams = new QueryParams<>(Account.class, limit, page);
        queryParams.setFilter(filterQuery);
        List<AccountResponseDTO> accounts = accountService.getAll(queryParams);
        return ResponseEntity.status(200).body(accounts);
    }

    // GET /accounts/{iban}
    @Parameter(in = ParameterIn.PATH, required = true, name ="iban", description = "The iban of the account you want to get.", schema = @Schema(type = "string"))
    @Operation(summary = "Get an account by id", description = "Get an account by id", tags = { "Accounts", "Employee", "Customer" } )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TransactionResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Bad Request!\",\"httpStatus\": \"BAD_REQUEST\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Unauthorized!\",\"httpStatus\": \"UNAUTHORIZED\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Forbidden!\",\"httpStatus\": \"FORBIDDEN\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Not Found!\",\"httpStatus\": \"NOT_FOUND\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Internal Server Error!\",\"httpStatus\": \"INTERNAL_SERVER_ERROR\",\"timestamp\": \"2001-01-01T00:00:00\"}")))
    })
    @PreAuthorize("hasAuthority('CUSTOMER') OR hasAuthority('EMPLOYEE')")
    @GetMapping(path = "/{iban}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAccountByIban(@PathVariable("iban") String iban) throws APIException {
        // Account account = CustomerIbanCheck(user, iban);
        
        AccountResponseDTO account = accountService.getAccountByIban(iban);
        return ResponseEntity.status(200).body(account);
    }

    // POST /accounts
    @Operation(summary = "Add an account", description = "Add an account", tags = { "Accounts", "Employee", "Customer" } )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TransactionResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Bad Request!\",\"httpStatus\": \"BAD_REQUEST\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Unauthorized!\",\"httpStatus\": \"UNAUTHORIZED\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Forbidden!\",\"httpStatus\": \"FORBIDDEN\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Not Found!\",\"httpStatus\": \"NOT_FOUND\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Internal Server Error!\",\"httpStatus\": \"INTERNAL_SERVER_ERROR\",\"timestamp\": \"2001-01-01T00:00:00\"}")))
    })
    @PreAuthorize("hasAuthority('CUSTOMER') OR hasAuthority('EMPLOYEE')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> insertAccount(@RequestBody @Valid AccountRequestDTO account) {

        AccountResponseDTO addedAccount = accountService.insertAccount(account);
        return ResponseEntity.status(201).body(addedAccount);
    }

    // PUT /accounts/{iban}
    @Parameter(in = ParameterIn.PATH, required = true, name ="iban", description = "The iban of the account you want to update.", schema = @Schema(type = "string"))
    @Operation(summary = "Update an account", description = "Update an account", tags = { "Accounts", "Employee" } )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TransactionResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Bad Request!\",\"httpStatus\": \"BAD_REQUEST\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Unauthorized!\",\"httpStatus\": \"UNAUTHORIZED\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Forbidden!\",\"httpStatus\": \"FORBIDDEN\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Not Found!\",\"httpStatus\": \"NOT_FOUND\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Internal Server Error!\",\"httpStatus\": \"INTERNAL_SERVER_ERROR\",\"timestamp\": \"2001-01-01T00:00:00\"}")))
    })
    @PreAuthorize("hasAuthority('EMPLOYEE') && #iban != @IBANGenerator.getNonStaticMeinBankIBAN()")
    @PutMapping(path = "/{iban}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateAccount(@RequestBody @Valid AccountRequestDTO account, @PathVariable("iban") String iban)
            throws APIException {
        AccountResponseDTO updatedAccount = accountService.updateAccount(account, iban);
        return ResponseEntity.status(200).body(updatedAccount);
    }

    // DELETE /accounts/{iban}
    @Parameter(in = ParameterIn.PATH, required = true, name ="iban", description = "The iban of the account you want to delete.", schema = @Schema(type = "string"))
    @Operation(summary = "Delete an account", description = "Delete an account", tags = { "Accounts", "Employee", "Customer" } )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "No Content", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Bad Request!\",\"httpStatus\": \"BAD_REQUEST\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Unauthorized!\",\"httpStatus\": \"UNAUTHORIZED\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Forbidden!\",\"httpStatus\": \"FORBIDDEN\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Not Found!\",\"httpStatus\": \"NOT_FOUND\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Internal Server Error!\",\"httpStatus\": \"INTERNAL_SERVER_ERROR\",\"timestamp\": \"2001-01-01T00:00:00\"}")))
    })
    @PreAuthorize("(hasAuthority('EMPLOYEE') OR hasAuthority('CUSTOMER')) && #iban != @IBANGenerator.getNonStaticMeinBankIBAN()")
    @DeleteMapping(path = "/{iban}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteAccount(@PathVariable("iban") String iban) {
        accountService.deleteAccount(iban);
        return ResponseEntity.status(204).body(null);
    }

    // GET /accounts/{iban}/transactions
    @Parameter(in = ParameterIn.QUERY, name ="filter", description = "Filter in the following format &lt;field&gt;&lt;operator&gt;'&lt;value&gt;' separated by a comma. Example: accountFrom.user.firstName:'John',accountTo.user.id:'1'", schema = @Schema(type = "string"))
    @Parameter(in = ParameterIn.QUERY, name ="limit", description = "The number of users that are to be returned (default: 12).", schema = @Schema(type = "int"))
    @Parameter(in = ParameterIn.QUERY, name ="page", description = "The page number (default: 0).", schema = @Schema(type = "int"))
    @Parameter(in = ParameterIn.PATH, required = true, name ="iban", description = "The iban of the account of which you want to get the transactions.", schema = @Schema(type = "string"))
    @Operation(summary = "Get all transactions of an account", description = "Get all transactions of an account", tags = { "Accounts", "Employee", "Customer" } )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = TransactionResponseDTO.class)))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Bad Request!\",\"httpStatus\": \"BAD_REQUEST\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Unauthorized!\",\"httpStatus\": \"UNAUTHORIZED\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Forbidden!\",\"httpStatus\": \"FORBIDDEN\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Not Found!\",\"httpStatus\": \"NOT_FOUND\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Internal Server Error!\",\"httpStatus\": \"INTERNAL_SERVER_ERROR\",\"timestamp\": \"2001-01-01T00:00:00\"}")))
    })
    @PreAuthorize("hasAuthority('CUSTOMER') OR hasAuthority('EMPLOYEE')")
    @GetMapping(path = "/{iban}/transactions", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getTransactions(@RequestParam(value = "filter", required = false) String filterQuery,
                                             @RequestParam(value = "limit", required = false) Integer limit,
                                             @RequestParam(value = "page", required = false) Integer page,
                                             @PathVariable("iban") String iban) throws Exception {
        QueryParams<Transaction> queryParams = new QueryParams<>(Transaction.class, limit, page);
        queryParams.setFilter(filterQuery);
        List<TransactionResponseDTO> accounts = accountService.getTransactions(queryParams, iban);
        return ResponseEntity.status(200).body(accounts);
    }

    // GET /accounts/{id}/balance
    @Parameter(in = ParameterIn.PATH, required = true, name ="iban", description = "The iban of which you want to get the balance.", schema = @Schema(type = "string"))
    @Operation(summary = "Get the balance of an account", description = "Get the balance of an account", tags = { "Accounts", "Employee", "Customer" } )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TransactionResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Bad Request!\",\"httpStatus\": \"BAD_REQUEST\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Unauthorized!\",\"httpStatus\": \"UNAUTHORIZED\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Forbidden!\",\"httpStatus\": \"FORBIDDEN\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Not Found!\",\"httpStatus\": \"NOT_FOUND\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Internal Server Error!\",\"httpStatus\": \"INTERNAL_SERVER_ERROR\",\"timestamp\": \"2001-01-01T00:00:00\"}")))
    })
    @PreAuthorize("hasAuthority('CUSTOMER') OR hasAuthority('EMPLOYEE')")
    @GetMapping(path = "/{iban}/balance", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getBalance(@PathVariable("iban") String iban) throws APIException {
        BalanceResponseDTO balance = accountService.getBalance(iban);
        return ResponseEntity.status(200).body(balance);
    }
}
