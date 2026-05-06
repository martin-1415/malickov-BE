package cz.malickov.backend.error;

import cz.malickov.backend.error.authExceptions.LoginFailedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalAuthExceptionHandler {

    /*
     * Find out and log who tried to access forbidden resource
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = "anonymous";
        if (authentication != null
                && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            email = authentication.getName();
        }

        log.warn("Unauthorized action attempted by user {}: {}", email, ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                Map.of(
                        "message", "You do not have permissions to perform this action."
                )
        );
    }

    @ExceptionHandler(LoginFailedException.class)
    public ResponseEntity<Map<String, String>> handleLoginFailedException(LoginFailedException ex) {
        String message = ex.getMessage();
        // no logs as logs are already in the services, email not found, etc.
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
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

        log.info("Validation error: " + errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body( errors);
    }
}
