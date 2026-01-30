package org.example.droppydriver.exceptions.userexceptions;

public class UserAlreadyExistsException extends CreateUserException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
