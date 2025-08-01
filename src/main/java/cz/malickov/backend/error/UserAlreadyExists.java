package cz.malickov.backend.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserAlreadyExists extends RuntimeException {
    public UserAlreadyExists(String email)
    {
        super("User with email : " + email + " already exists");
    }
}
