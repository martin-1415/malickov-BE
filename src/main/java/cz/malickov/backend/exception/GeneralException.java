package cz.malickov.backend.exception;


/*
 * This exception is used to send whatever error to FE which is
 *  not related to any specific class
 */
public class GeneralException extends RuntimeException {
    public GeneralException(String message) {
        super(message);
    }
}
