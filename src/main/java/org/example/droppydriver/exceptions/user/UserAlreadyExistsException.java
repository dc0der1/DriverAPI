package org.example.droppydriver.exceptions.user;

public class UserAlreadyExistsException extends CreateUserException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
