package cz.malickov.backend.error;

public class GeneralException extends RuntimeException {
    public GeneralException(String message) {
        super(message);
    }
}
