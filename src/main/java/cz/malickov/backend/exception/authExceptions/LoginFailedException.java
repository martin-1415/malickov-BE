package cz.malickov.backend.exception.authExceptions;

public class LoginFailedException extends RuntimeException {

    public LoginFailedException() {
        super("Login failed.");
    }
}
