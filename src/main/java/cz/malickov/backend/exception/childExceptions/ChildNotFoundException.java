package cz.malickov.backend.exception.childExceptions;

import java.util.UUID;

public class ChildNotFoundException extends RuntimeException {
    public ChildNotFoundException(String message)
    {
        super(message);
    }

    public ChildNotFoundException(UUID uuid)
    {
        super( "Child with uuid: " + uuid + " not found");
    }
}
