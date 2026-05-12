package cz.malickov.backend.exception;


import cz.malickov.backend.exception.utilsExceptions.IdentifierNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalUtilsExceptionHandler {


    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<Map<String, String>> GeneralException(GeneralException ex) {
        String message = ex.getMessage();
        log.info(message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of(
                        "message", message
                )
        );
    }


    @ExceptionHandler(IdentifierNotFoundException.class)
    public ResponseEntity<Map<String, String>> IdentifierException(IdentifierNotFoundException ex) {
        String message = ex.getMessage();
        log.info(message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of(
                        "message", message
                )
        );
    }

}
