package nl.inholland.codegeneration.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
public class APIExceptionTest {


  @Test
  void testAPIException() {
    // Set up mock values
    String errorMessage = "Test error message";
    LocalDateTime timestamp = LocalDateTime.now();

    HttpStatus httpStatus = HttpStatus.ACCEPTED;
    // Create an instance of APIException
    APIException apiException = new APIException(errorMessage, httpStatus, timestamp);

    // Verify the exception properties
    assertEquals(errorMessage, apiException.getMessage());
    assertEquals(httpStatus, apiException.getHttpStatus());
    assertEquals(timestamp, apiException.getTimestamp());
  }
}
