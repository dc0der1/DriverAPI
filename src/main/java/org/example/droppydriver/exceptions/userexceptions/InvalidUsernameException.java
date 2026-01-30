package org.example.droppydriver.exceptions.userexceptions;

public class InvalidUsernameException extends CreateUserException {
    public InvalidUsernameException(String message) {
        super(message);
    }
}
