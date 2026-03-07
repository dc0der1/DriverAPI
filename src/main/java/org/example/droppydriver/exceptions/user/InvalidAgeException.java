package org.example.droppydriver.exceptions.user;

public class InvalidAgeException extends CreateUserException {
    public InvalidAgeException(String message) {
        super(message);
    }
}
