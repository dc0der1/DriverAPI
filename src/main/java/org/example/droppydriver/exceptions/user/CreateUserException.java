package org.example.droppydriver.exceptions.user;

public class CreateUserException extends RuntimeException {
    public CreateUserException() {}

    public CreateUserException(String message) {
        super(message);
    }

    public CreateUserException(String message, Throwable innerException) {
        super(message, innerException);
    }
}
