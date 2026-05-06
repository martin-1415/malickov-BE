package cz.malickov.backend.error.userExceptions;

public class UserAlreadyExistsException extends RuntimeException {
  public UserAlreadyExistsException(String email) {
      super("User with email: " + email + " not found");
  }
}
