package org.example.droppydriver.exceptions.userexceptions;

public class NoSuchUserException extends GetUserException {
    public NoSuchUserException(String message) {
        super(message);
    }
}
