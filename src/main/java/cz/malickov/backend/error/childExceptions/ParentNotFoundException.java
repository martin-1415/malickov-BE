package cz.malickov.backend.error.childExceptions;

public class ParentNotFoundException extends RuntimeException {
    public ParentNotFoundException(String uuid)
    {
        super("Parent with uuid: " + uuid + " not found");
    }
}
