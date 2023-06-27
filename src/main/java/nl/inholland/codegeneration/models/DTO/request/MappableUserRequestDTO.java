package nl.inholland.codegeneration.models.DTO.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import nl.inholland.codegeneration.models.MinAge;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface MappableUserRequestDTO {
    List<Integer> roles();
    String username();
    String password();
    String firstName();
    String lastName();
    String email();
    String phoneNumber();
    LocalDate birthdate();
    BigDecimal dayLimit();
    BigDecimal transactionLimit();
}
