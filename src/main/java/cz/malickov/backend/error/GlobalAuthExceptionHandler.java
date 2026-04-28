package cz.malickov.backend.error;

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

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = "anonymous";
        if (authentication != null
                && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            email = authentication.getName();
        }

        log.info("Unauthorized action attempted by user {}.",email);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                Map.of(
                        "message", "Good morning Mr./Mrs. "+email+", but you do not have permission to perform this action."
                )
        );
    }

    @ExceptionHandler(LoginFailedException.class)
    public ResponseEntity<Map<String, String>> loginFailedException() {
        log.info("Wrong email or password or user inactive.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                Map.of(
                        "message", "Wrong email or password or user inactive."
                )
        );
    }

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<Map<String, String>> GeneralException(GeneralException ex) {
        String message = ex.getMessage();
        log.info(message);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                Map.of(
                        "message", message
                )
        );
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, String>> authorizationFailedException() {
        log.error("Authentication failed.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                Map.of(
                        "message", "Authentication failed."
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
        log.info("User not found:" + message);
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
