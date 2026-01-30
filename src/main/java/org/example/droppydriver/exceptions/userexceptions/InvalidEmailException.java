package org.example.droppydriver.exceptions.userexceptions;

public class InvalidEmailException extends CreateUserException {
    public InvalidEmailException(String message) {
        super(message);
    }
}
