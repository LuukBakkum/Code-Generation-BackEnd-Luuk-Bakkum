package nl.inholland.codegeneration.exceptions;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

public class APIException extends Exception {
    private final HttpStatus httpStatus;
    private final LocalDateTime timestamp;

    public APIException(String errorMessage, HttpStatus httpStatus, LocalDateTime timestamp) {
        super(errorMessage);
        this.httpStatus = httpStatus;
        this.timestamp = timestamp;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
}
