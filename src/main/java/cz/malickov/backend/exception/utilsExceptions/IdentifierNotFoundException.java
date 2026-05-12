package cz.malickov.backend.exception.utilsExceptions;

public class IdentifierNotFoundException extends RuntimeException {
    public IdentifierNotFoundException(int identifierId)
    {
        super("Identifier with ID "+" "+ identifierId + " not found");
    }
}
