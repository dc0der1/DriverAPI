package org.example.droppydriver.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.example.droppydriver.exceptions.file.FileAlreadyExistsException;
import org.example.droppydriver.exceptions.file.FileNotFoundException;
import org.example.droppydriver.exceptions.folder.FolderAlreadyExistException;
import org.example.droppydriver.exceptions.folder.NoSuchFolderException;
import org.example.droppydriver.exceptions.user.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.NoSuchFileException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler({UserAlreadyExistsException.class, FolderAlreadyExistException.class,
            FileAlreadyExistsException.class})
    public ResponseEntity<Map<String, String>> handleUserExists(Exception e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("message", "Already exists", "error", e.getMessage()));
    }

    @ExceptionHandler({InvalidPasswordException.class, InvalidEmailException.class,
            InvalidUsernameException.class, InvalidAgeException.class,
            InvalidLoginException.class})
    public ResponseEntity<Map<String, String>> handleValidationExceptions(Exception e) {
        return ResponseEntity.badRequest()
                .body(Map.of("message", "Validation Error", "error", e.getMessage()));
    }

    @ExceptionHandler({UserNotFoundException.class, NoSuchFolderException.class,
            FileNotFoundException.class})
    public ResponseEntity<Void> handleNotFound() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleDTOValidationExceptions(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();

        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        Map<String, Object> body = new HashMap<>();
        body.put("message", "Validation failed");
        body.put("errors", errors);

        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralError(Exception e) {
        log.error(e.getMessage());
        return ResponseEntity.internalServerError()
                .body(Map.of("message", "An unexpected error occurred"));
    }
}
