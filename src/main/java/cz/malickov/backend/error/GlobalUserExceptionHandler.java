package cz.malickov.backend.error;

import cz.malickov.backend.error.userExceptions.UserAlreadyExistsException;
import cz.malickov.backend.error.userExceptions.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalUserExceptionHandler {


    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        String message = ex.getMessage();
        log.info(message);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                Map.of(
                        "message", message
                )
        );
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFoundException(UserNotFoundException ex) {
        String message = ex.getMessage();
        log.info(message);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of(
                        "message", message
                )
        );
    }
}
