package org.example.droppydriver.exceptions.user;

public class NoUsersException extends RuntimeException {
    public NoUsersException(String message) {
        super(message);
    }
}
