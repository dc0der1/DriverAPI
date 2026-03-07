package org.example.droppydriver.exceptions.user;

public class GetUserException extends IllegalArgumentException {
    public GetUserException(String message) {
        super(message);
    }
}
