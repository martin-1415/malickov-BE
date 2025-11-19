package cz.malickov.backend.error;

import org.springframework.http.HttpStatus;

public class LoginFailedException extends RuntimeException {
    public LoginFailedException() {
    }
}
