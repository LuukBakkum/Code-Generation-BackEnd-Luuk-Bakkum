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
import io.swagger.v3.oas.annotations.security.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nl.inholland.codegeneration.models.DTO.request.UserRequestDTO;
import nl.inholland.codegeneration.models.DTO.request.UserUpdateRequestDTO;
import nl.inholland.codegeneration.models.DTO.response.APIExceptionResponseDTO;
import nl.inholland.codegeneration.models.DTO.response.AccountResponseDTO;
import nl.inholland.codegeneration.models.DTO.response.UserResponseDTO;
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

import nl.inholland.codegeneration.exceptions.APIException;
import nl.inholland.codegeneration.models.Account;
import nl.inholland.codegeneration.models.QueryParams;
import nl.inholland.codegeneration.models.User;
import nl.inholland.codegeneration.services.AccountService;
import nl.inholland.codegeneration.services.UserService;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Users endpoints")
@SecurityRequirement(name = "bearerToken")
public class UserController {
    private final UserService userService;
    private final AccountService accountService;

    @Parameter(in = ParameterIn.QUERY, name ="filter", description = "Filter in the following format &lt;field&gt;&lt;operator&gt;'&lt;value&gt;' separated by a comma. Example: firstName:'John',lastName:'Doe'", schema = @Schema(type = "string"))
    @Parameter(in = ParameterIn.QUERY, name ="limit", description = "The number of users that are to be returned (default: 12).", schema = @Schema(type = "int"))
    @Parameter(in = ParameterIn.QUERY, name ="page", description = "The page number (default: 0).", schema = @Schema(type = "int"))
    @Parameter(in = ParameterIn.QUERY, name ="hasAccount", description = "Check if the user has an account (default: both).", schema = @Schema(type = "boolean"))
    @Operation(summary = "Get all users", description = "Get all users", tags = { "Users", "Employee" } )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserResponseDTO.class)))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Bad Request!\",\"httpStatus\": \"BAD_REQUEST\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Unauthorized!\",\"httpStatus\": \"UNAUTHORIZED\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Forbidden!\",\"httpStatus\": \"FORBIDDEN\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Internal Server Error!\",\"httpStatus\": \"INTERNAL_SERVER_ERROR\",\"timestamp\": \"2001-01-01T00:00:00\"}")))
    })
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserResponseDTO>> getAll(@RequestParam(value = "filter", required = false) String filterQuery,
                                                        @RequestParam(value = "limit", required = false) Integer limit,
                                                        @RequestParam(value = "page", required = false) Integer page,
                                                        @RequestParam(value = "hasAccount", required = false) Boolean hasAccount) throws Exception {
        QueryParams<User> queryParams = new QueryParams<>(User.class, limit, page);
        queryParams.setFilter(filterQuery);
        List<UserResponseDTO> users = userService.getAll(queryParams, hasAccount);
        return ResponseEntity.status(200).body(users);
    }

    @Parameter(in = ParameterIn.PATH, required = true, name ="id", description = "The id of the user you want to get.", schema = @Schema(type = "int"))
    @Operation(summary = "Get user by id", description = "Get user by id", tags = { "Users", "Employee" } )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Bad Request!\",\"httpStatus\": \"BAD_REQUEST\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Unauthorized!\",\"httpStatus\": \"UNAUTHORIZED\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Forbidden!\",\"httpStatus\": \"FORBIDDEN\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Not Found!\",\"httpStatus\": \"NOT_FOUND\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Internal Server Error!\",\"httpStatus\": \"INTERNAL_SERVER_ERROR\",\"timestamp\": \"2001-01-01T00:00:00\"}")))
    })
    @PreAuthorize("hasAuthority('EMPLOYEE') OR (hasAuthority('CUSTOMER') AND #id == authentication.principal.id)")
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponseDTO> getById(@PathVariable Long id) {
        UserResponseDTO user = userService.getById(id);
        return ResponseEntity.status(200).body(user);
    }

    @Parameter(in = ParameterIn.QUERY, name ="filter", description = "Filter in the following format &lt;field&gt;&lt;operator&gt;'&lt;value&gt;' separated by a comma. Example: user.firstName:'John',user.id:'1'", schema = @Schema(type = "string"))
    @Parameter(in = ParameterIn.QUERY, name ="limit", description = "The number of users that are to be returned (default: 12).", schema = @Schema(type = "int"))
    @Parameter(in = ParameterIn.QUERY, name ="page", description = "The page number (default: 0).", schema = @Schema(type = "int"))
    @Parameter(in = ParameterIn.PATH, required = true, name ="id", description = "The id of the user of which you want to get the accounts.", schema = @Schema(type = "int"))
    @Operation(summary = "Get all accounts of a user", description = "Get all accounts of a user", tags = { "Users", "Employee", "Customer" } )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = AccountResponseDTO.class)))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Bad Request!\",\"httpStatus\": \"BAD_REQUEST\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Unauthorized!\",\"httpStatus\": \"UNAUTHORIZED\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Forbidden!\",\"httpStatus\": \"FORBIDDEN\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Not Found!\",\"httpStatus\": \"NOT_FOUND\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Internal Server Error!\",\"httpStatus\": \"INTERNAL_SERVER_ERROR\",\"timestamp\": \"2001-01-01T00:00:00\"}")))
    })
    @PreAuthorize("hasAuthority('EMPLOYEE') OR ((hasAuthority('CUSTOMER') AND #id == authentication.principal.id))")
    @GetMapping(path = "/{id}/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AccountResponseDTO>> getAllAccountsById(@RequestParam(value = "filter", required = false) String filterQuery,
                                                @RequestParam(value = "limit", required = false) Integer limit,
                                                @RequestParam(value = "page", required = false) Integer page,
                                                @PathVariable Long id) throws Exception {
        QueryParams<Account> queryParams = new QueryParams<>(Account.class, limit, page);
        queryParams.setFilter(filterQuery);
        List<AccountResponseDTO> accounts = accountService.getAllByUserId(queryParams, id);
        return ResponseEntity.status(200).body(accounts);
    }

    @Operation(summary = "Add a user", description = "Add a user", tags = { "Users", "Employee" } )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Bad Request!\",\"httpStatus\": \"BAD_REQUEST\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Unauthorized!\",\"httpStatus\": \"UNAUTHORIZED\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Forbidden!\",\"httpStatus\": \"FORBIDDEN\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Internal Server Error!\",\"httpStatus\": \"INTERNAL_SERVER_ERROR\",\"timestamp\": \"2001-01-01T00:00:00\"}")))
    })
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponseDTO> add(@RequestBody @Valid UserRequestDTO user) {
        UserResponseDTO addedUser = userService.add(user);
        return ResponseEntity.status(201).body(addedUser);
    }

    @Parameter(in = ParameterIn.PATH, required = true, name ="id", description = "The id of the user you want to update.", schema = @Schema(type = "int"))
    @Operation(summary = "Update a user", description = "Update a user", tags = { "Users", "Employee" } )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Bad Request!\",\"httpStatus\": \"BAD_REQUEST\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Unauthorized!\",\"httpStatus\": \"UNAUTHORIZED\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Forbidden!\",\"httpStatus\": \"FORBIDDEN\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Not Found!\",\"httpStatus\": \"NOT_FOUND\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Internal Server Error!\",\"httpStatus\": \"INTERNAL_SERVER_ERROR\",\"timestamp\": \"2001-01-01T00:00:00\"}")))
    })
    @PreAuthorize("hasAuthority('EMPLOYEE') && #id != 1")
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponseDTO> update(@RequestBody @Valid UserUpdateRequestDTO user, @PathVariable Long id) {
        UserResponseDTO updatedUser = userService.update(user, id);
        return ResponseEntity.status(200).body(updatedUser);
    }

    @Parameter(in = ParameterIn.PATH, required = true, name ="id", description = "The id of the user you want to delete.", schema = @Schema(type = "int"))
    @Operation(summary = "Delete a user", description = "Delete a user", tags = { "Users", "Employee" } )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "No Content", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Bad Request!\",\"httpStatus\": \"BAD_REQUEST\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Unauthorized!\",\"httpStatus\": \"UNAUTHORIZED\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Forbidden!\",\"httpStatus\": \"FORBIDDEN\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Not Found!\",\"httpStatus\": \"NOT_FOUND\",\"timestamp\": \"2001-01-01T00:00:00\"}"))),
        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIExceptionResponseDTO.class), examples = @ExampleObject(value = "{\"message\": \"Internal Server Error!\",\"httpStatus\": \"INTERNAL_SERVER_ERROR\",\"timestamp\": \"2001-01-01T00:00:00\"}")))
    })
    @PreAuthorize("hasAuthority('EMPLOYEE') && #id != 1")
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) throws APIException {
        userService.delete(id);
        return ResponseEntity.status(204).body("No Content");
    }
}