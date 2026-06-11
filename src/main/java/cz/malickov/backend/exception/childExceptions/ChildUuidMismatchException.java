package cz.malickov.backend.exception.childExceptions;

import java.util.UUID;

public class ChildUuidMismatchException extends RuntimeException {
    public ChildUuidMismatchException(UUID pathUuid, UUID bodyUuid) {
        super("Path uuid (" + pathUuid + ") and payload uuid (" + bodyUuid + ") mismatch.");
    }
}
