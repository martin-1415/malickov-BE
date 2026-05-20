package cz.malickov.backend.exception.userExceptions;

public class UserAlreadyExistsException extends RuntimeException {
  public UserAlreadyExistsException(String email) {
      super("User with email: " + email + " already exists");
  }
}
