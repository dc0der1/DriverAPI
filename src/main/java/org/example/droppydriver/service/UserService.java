package org.example.droppydriver.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.droppydriver.dtos.CreateUserRequest;
import org.example.droppydriver.dtos.LoginUserRequest;
import org.example.droppydriver.dtos.UserResponse;
import org.example.droppydriver.exceptions.userexceptions.*;
import org.example.droppydriver.models.User;
import org.example.droppydriver.repository.IUserRepository;
import org.example.droppydriver.utility.ValidationUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.security.auth.login.LoginException;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements IUserService {

    private final IUserRepository userRepository;
    private final JwtService jwtService;

    @Override
    public User registerUser(CreateUserRequest request) throws CreateUserException {

        //var passwordErrors = new ArrayList<PasswordError>();

        if (request.getEmail() == null || request.getEmail().isBlank() || !request.getEmail().contains("@")) {
            throw new InvalidEmailException("Invalid email");
        }

        if (request.getPassword().isBlank() || request.getPassword().length() < 8) {
            throw new InvalidPasswordException("Password is too short");
        }

        if (!request.getPassword().matches(".*[0-9].*")) {
            throw new InvalidPasswordException("Password requires at least one number");
        }

        if (!request.getPassword().matches(".*[A-Z].*")) {
            throw new InvalidPasswordException("Password requires at least one upper case");
        }

        if (!request.getPassword().matches(".*[^a-zA-Z0-9].*")) {
            throw new InvalidPasswordException("Password requires at least one symbol");
        }

        if (userRepository.existsUserByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("User already exists: " + request.getEmail());
        }

        if (request.getUsername().length() < 8) {
            throw new InvalidUsernameException("Username is too short");
        }

        if (request.getAge() < 18 || request.getAge() > 120) {
            throw new InvalidAgeException("Your age has to be between 18 and 120");
        }

        String hashedPassword = BCrypt.withDefaults().hashToString(12, request.getPassword().toCharArray());

        var user = new User(request.getUsername(), hashedPassword, request.getEmail(), request.getAge());

        userRepository.save(user);
        log.info("User {} has been created", user.getUsername());

        return user;
    }



    @Override
    public Stream<UserResponse> findAllUsers() {

        if (userRepository.findAll().isEmpty()) {
            throw new NoUsersException("No users found");
        }

        return userRepository.findAll()
                .stream()
                .map(UserResponse::fromModel);
    }

    @Override
    public UserResponse findUserById(String id) throws GetUserException {

        if (userRepository.findById(UUID.fromString(id)).isEmpty()) {
            throw new NoUsersException("No user found with id: " + id);
        }

        if (!ValidationUtils.isValidUUID(id)) {
            throw new IllegalArgumentException("Invalid user id: " + id);
        }

        return UserResponse.fromModel(userRepository.findById(UUID.fromString(id)).get());
    }

    @Override
    public String signInUser(LoginUserRequest request) {

        if (!userRepository.existsUserByEmail(request.getEmail())) {
            throw new InvalidLoginException("Invalid email or password");
        }

        var user = userRepository.findUserByEmail(request.getEmail());
        assert user.getPassword() != null;
        BCrypt.Result result = BCrypt.verifyer().verify(
                request.getPassword().toCharArray(),
                user.getPassword()
        );

        if (!result.verified) {
            throw new InvalidLoginException("Invalid email or password");
        }

        return jwtService.generateToken(user.getId());
    }
}
