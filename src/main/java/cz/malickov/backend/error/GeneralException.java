package cz.malickov.backend.error;


/*
 * This exception is used to send whatever error to FE which is
 *  not related to any specific class
 */
public class GeneralException extends RuntimeException {
    public GeneralException(String message) {
        super(message);
    }
}
