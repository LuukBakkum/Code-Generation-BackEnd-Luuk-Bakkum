package nl.inholland.codegeneration.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import nl.inholland.codegeneration.models.DTO.response.APIExceptionResponseDTO;

@ExtendWith(MockitoExtension.class)
public class APIExceptionHandlerTest {
  @Mock
  private WebRequest webRequest;

  @Mock
  private APIException apiException;

  private APIExceptionHandler apiExceptionHandler;

  @BeforeEach
  public void setup() {

    apiExceptionHandler = new APIExceptionHandler();
  }

  @Test
  void testHandleAPIException() {

    HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

    apiException = new APIException("Test exception", HttpStatus.BAD_REQUEST, LocalDateTime.now());

    ResponseEntity<APIExceptionResponseDTO> response = apiExceptionHandler.handleAPIException(apiException, webRequest);

    assertNotNull(response);
    assertEquals(httpStatus, response.getStatusCode());

    APIExceptionResponseDTO responseBody = response.getBody();
    assertNotNull(responseBody);
    assertEquals("Test exception", responseBody.message());
    assertEquals(httpStatus, responseBody.httpStatus());
    assertNotNull(responseBody.timestamp());

  }

  @Test
  void testHandleNotFoundException() {
    HttpStatus httpStatus = HttpStatus.NOT_FOUND;

     apiException = new APIException("Test exception", httpStatus, LocalDateTime.now());

    ResponseEntity<APIExceptionResponseDTO> response = apiExceptionHandler.handleNotFoundException(apiException, webRequest);

    assertNotNull(response);
    assertEquals(httpStatus, response.getStatusCode());

    APIExceptionResponseDTO responseBody = response.getBody();
    assertNotNull(responseBody);
    assertEquals("Test exception", responseBody.message());
    assertEquals(httpStatus, responseBody.httpStatus());
    assertNotNull(responseBody.timestamp());
  }

  @Test
  void testHandleForbiddenException() {

    HttpStatus httpStatus = HttpStatus.FORBIDDEN;
    apiException = new APIException("Test exception", httpStatus, LocalDateTime.now());

    ResponseEntity<APIExceptionResponseDTO> response = apiExceptionHandler.handleForbiddenException(apiException, webRequest);

    assertNotNull(response);
    assertEquals(httpStatus, response.getStatusCode());

    APIExceptionResponseDTO responseBody = response.getBody();
    assertNotNull(responseBody);
    assertEquals("Test exception", responseBody.message());
    assertEquals(httpStatus, responseBody.httpStatus());
    assertNotNull(responseBody.timestamp());
  }

  @Test
  void testHandleUnauthorizedException() {
    HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;
    apiException = new APIException("Test exception", httpStatus, LocalDateTime.now());

    ResponseEntity<APIExceptionResponseDTO> response = apiExceptionHandler.handleUnauthorizedException(apiException, webRequest);

    assertNotNull(response);
    assertEquals(httpStatus, response.getStatusCode());

    APIExceptionResponseDTO responseBody = response.getBody();
    assertNotNull(responseBody);
    assertEquals("Test exception", responseBody.message());
    assertEquals(httpStatus, responseBody.httpStatus());
    assertNotNull(responseBody.timestamp());
  }

  @Test
  void testHandleBadRequestException() {
    HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
    apiException = new APIException("Test exception", httpStatus, LocalDateTime.now());

    ResponseEntity<APIExceptionResponseDTO> response = apiExceptionHandler.handleBadRequestException(apiException, webRequest);

    assertNotNull(response);
    assertEquals(httpStatus, response.getStatusCode());

    APIExceptionResponseDTO responseBody = response.getBody();
    assertNotNull(responseBody);
    assertEquals("Test exception", responseBody.message());
    assertEquals(httpStatus, responseBody.httpStatus());
    assertNotNull(responseBody.timestamp());
    }

  @Test
  void testHandleBadRequestExceptionByConstraint() {
    HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
    apiException = new APIException("Test exception", httpStatus, LocalDateTime.now());

    ResponseEntity<APIExceptionResponseDTO> response = apiExceptionHandler.handleBadRequestExceptionByConstraint(apiException, webRequest);

    assertNotNull(response);
    assertEquals(httpStatus, response.getStatusCode());

    APIExceptionResponseDTO responseBody = response.getBody();
    assertNotNull(responseBody);
    assertEquals("Bad Request!", responseBody.message());
    assertEquals(httpStatus, responseBody.httpStatus());
    assertNotNull(responseBody.timestamp());
  }

  @Test
  void testHandleException() {
    HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    Exception exception = new Exception("Test exception");
    ResponseEntity<APIExceptionResponseDTO> response = apiExceptionHandler.handleException(exception, webRequest);

    assertNotNull(response);
    assertEquals(httpStatus, response.getStatusCode());

    APIExceptionResponseDTO responseBody = response.getBody();
    assertNotNull(responseBody);
    assertEquals("Test exception", responseBody.message());
    assertEquals(httpStatus, responseBody.httpStatus());
    assertNotNull(responseBody.timestamp());
  }

}
