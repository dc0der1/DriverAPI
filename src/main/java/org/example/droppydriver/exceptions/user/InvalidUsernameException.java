package org.example.droppydriver.exceptions.user;

public class InvalidUsernameException extends CreateUserException {
    public InvalidUsernameException(String message) {
        super(message);
    }
}
