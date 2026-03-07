package org.example.droppydriver.exceptions.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvalidPasswordException extends CreateUserException{

    public InvalidPasswordException(String message) {
        super(message);
    }
}
