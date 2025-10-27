package cz.malickov.backend.error;

public class UserAlreadyExistsException extends RuntimeException {
  public UserAlreadyExistsException(String message) {
      super(message);
  }
}
