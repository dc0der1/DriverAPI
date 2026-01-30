package org.example.droppydriver.exceptions.folderexceptions;

public class FolderAlreadyExistException extends RuntimeException {
    public FolderAlreadyExistException(String message) {
        super(message);
    }
}
