package cz.malickov.backend.error;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


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

}
