package org.example.droppydriver.exceptions.user;

public class NoSuchUserException extends GetUserException {
    public NoSuchUserException(String message) {
        super(message);
    }
}
