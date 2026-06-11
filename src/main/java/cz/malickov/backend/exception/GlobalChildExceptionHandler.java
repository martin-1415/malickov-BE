package cz.malickov.backend.exception;

import cz.malickov.backend.exception.childExceptions.ChildNotFoundException;
import cz.malickov.backend.exception.childExceptions.ChildUuidMismatchException;
import cz.malickov.backend.exception.childExceptions.ParentNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalChildExceptionHandler {

    @ExceptionHandler(ChildUuidMismatchException.class)
    public ResponseEntity<Map<String, String>> handleChildUuidMismatchException(ChildUuidMismatchException ex) {
        String message = ex.getMessage();
        log.info(message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of(
                        "message", message
                )
        );
    }

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
