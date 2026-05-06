package cz.malickov.backend.error;

import cz.malickov.backend.error.childExceptions.ChildNotFoundException;
import cz.malickov.backend.error.childExceptions.ParentNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalChildExceptionHandler {

    @ExceptionHandler(ParentNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleParentNotFoundException(ParentNotFoundException ex) {
        String message = ex.getMessage();
        log.info(message);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of(
                        "message", message
                )
        );
    }

    @ExceptionHandler(ChildNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleChildNotFoundException(ChildNotFoundException ex) {
        String message = ex.getMessage();
        log.info(message);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of(
                        "message", message
                )
        );
    }
}
