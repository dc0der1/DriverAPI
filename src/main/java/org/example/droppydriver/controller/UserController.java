package org.example.droppydriver.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.droppydriver.dtos.CreateUserRequest;
import org.example.droppydriver.dtos.LoginUserRequest;
import org.example.droppydriver.dtos.UserResponse;
import org.example.droppydriver.exceptions.userexceptions.*;
import org.example.droppydriver.service.IUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/droppydriver/user")
@Slf4j
public class UserController {

    private final IUserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginUserRequest request) {
        try {
            var result = userService.signInUser(request);
            return ResponseEntity.ok(result);
        } catch (InvalidLoginException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "message: ", e.getMessage()
                    ));
        } catch (Exception e) {
            log.error("Error while creating folder", e);
            return ResponseEntity.badRequest().body(new LoginUserRequest());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody CreateUserRequest request) {
        try {
            var user = userService.registerUser(request);
            return ResponseEntity.created(URI.create("/droppydriver/user/register")).body(UserResponse.fromModel(user));
        } catch (InvalidPasswordException exception) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "message", "Invalid password",
                            "errors", exception.getMessage()
                    ));
        } catch (InvalidEmailException exception) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                          "message", "Invalid email",
                            "error", exception.getMessage()
                    ));
        } catch (UserAlreadyExistsException exception) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "message", "Email already exists",
                            "error", exception.getMessage()
                    ));
        } catch (InvalidUsernameException exception) {
            return  ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "message", "Invalid username",
                            "error", exception.getMessage()
                    ));
        } catch (InvalidAgeException exception) {
            return  ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "message", "Invalid age",
                            "error", exception.getMessage()
                    ));
        } catch (Exception exception) {
            log.error("Error while creating folder", exception);
            return ResponseEntity
                    .internalServerError()
                    .body(Map.of(
                            "message", "Unexpected error"
                    ));
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        try {
            return ResponseEntity.ok(userService.findAllUsers());
        } catch (NoUsersException exception) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "error", exception.getMessage()
                    ));
        } catch (Exception exception) {
            log.error("Error while getting users", exception);
            return ResponseEntity
                    .internalServerError()
                    .body(Map.of(
                            "error", "Unexpected error"
                    ));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<?> getUserById(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(userService.findUserById(id.toString()));
        } catch (NoUsersException | IllegalArgumentException exception) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "error", exception.getMessage()
                    ));
        } catch (Exception exception) {
            log.error("Error while getting user by id", exception);
            return ResponseEntity
                    .internalServerError()
                    .body(Map.of(
                            "error", "Unexpected error"
                    ));
        }
    }
}
