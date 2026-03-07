package org.example.droppydriver.exceptions.folder;

public class FolderAlreadyExistException extends RuntimeException {
    public FolderAlreadyExistException(String message) {
        super(message);
    }
}
