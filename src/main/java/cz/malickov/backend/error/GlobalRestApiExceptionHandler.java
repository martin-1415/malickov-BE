package cz.malickov.backend.error;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalRestApiExceptionHandler {

    // Custum Api Exceptions
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(ApiException ex) {
        return ResponseEntity.status(ex.getStatus()).body(
                Map.of(
                        "message", ex.getMessage()
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
        return ResponseEntity.badRequest().body( errors);
    }
}
