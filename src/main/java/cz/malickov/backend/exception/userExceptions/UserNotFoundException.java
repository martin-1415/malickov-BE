package cz.malickov.backend.exception.userExceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        // no general message can be set here, as it could be connected with email or UUID
        super(message);
        log.info(message);
    }
}

