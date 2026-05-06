package cz.malickov.backend.error.authExceptions;

public class LoginFailedException extends RuntimeException {

    public LoginFailedException() {
        super("Login failed.");
    }
}
