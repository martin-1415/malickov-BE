package cz.malickov.backend.error;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(UserAlreadyExists.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(UserAlreadyExists ex) {
        return ResponseEntity.badRequest().body(
                Map.of(
                        "error", "Bad Request",
                        "message", ex.getMessage()
                )
        );
    }
}
