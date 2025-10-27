package cz.malickov.backend.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalRestApiExceptionHandler {


    @ExceptionHandler(LoginException.class)
    public ResponseEntity<Map<String, String>> loginFailedException() {
        log.error("Wrong email or password.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                Map.of(
                        "message", "Wrong email or password."
                )
        );
    }


    @ExceptionHandler(AuthorizationFailedException.class)
    public ResponseEntity<Map<String, String>> authorizationFailedException() {
        log.error("Authorization failed.");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                Map.of(
                        "message", "Authorization failed."
                )
        );
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> userExistsException(UserAlreadyExistsException ex) {
        String message = ex.getMessage();
        log.error(message);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                Map.of(
                        "message", message
                )
        );
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> userNotFoundException(UserNotFoundException ex) {
        String message = ex.getMessage();
        log.error("User not found:" + message);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of(
                        "message", message
                )
        );
    }

    // Exceptions for argument validation, only bad requests type
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<HashMap<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        HashMap<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        log.error("Validation error: " + errors);

        return ResponseEntity.badRequest().body( errors);
    }
}
