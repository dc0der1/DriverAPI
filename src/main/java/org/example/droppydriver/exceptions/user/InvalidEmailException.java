package org.example.droppydriver.exceptions.user;

public class InvalidEmailException extends CreateUserException {
    public InvalidEmailException(String message) {
        super(message);
    }
}
