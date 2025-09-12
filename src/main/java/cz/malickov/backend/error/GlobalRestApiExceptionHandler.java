package cz.malickov.backend.error;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalRestApiExceptionHandler {


    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(ApiException ex) {
        return ResponseEntity.badRequest().body(
                Map.of(
                        "error", ex.getStatus().toString(),
                        "message", ex.getMessage()
                )
        );
    }

}
