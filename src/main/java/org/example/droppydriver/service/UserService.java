package org.example.droppydriver.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.droppydriver.dtos.CreateUserRequest;
import org.example.droppydriver.dtos.LoginUserRequest;
import org.example.droppydriver.dtos.UserResponse;
import org.example.droppydriver.exceptions.userexceptions.*;
import org.example.droppydriver.models.Role;
import org.example.droppydriver.models.User;
import org.example.droppydriver.repository.IUserRepository;
import org.example.droppydriver.utility.ValidationUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements IUserService {

    private final IUserRepository userRepository;
    private final IJwtService jwtService;

    /**
     * Register a user to the system.
     * <p>
     * This method validates username, email, password and age. When all these fields are correct then the user
     * will be registered to the system.
     * </p>
     *
     * @param request this is a DTO request that asks for username, email, password and age for registration
     * @return the newly registered user {@link User}
     * @throws InvalidEmailException if the email is invalid or doesn't contain this symbol '@'
     * @throws InvalidPasswordException if the password is invalid e.g. doesn't contain number, upper & lowercase or symbol
     * @throws UserAlreadyExistsException if the user email already exists
     * @throws InvalidAgeException if the age input is unrealistic such as 3 years old or 1000 years old
     * @throws InvalidUsernameException if the username is too short
     */
    @Override
    public User registerUser(CreateUserRequest request) throws CreateUserException {

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

        var user = new User(request.getUsername(), hashedPassword, request.getEmail(), request.getAge(), Role.USER);

        userRepository.save(user);
        log.info("User {} has been created", user.getUsername());

        return user;
    }

    /**
     * Finds all users
     * <p>
     * This is a method that finds all users. This method is only accessed by the admin.
     * </p>
     *
     * @return a list of UserResponse that contains user id, username, email, age and list of folders
     * @throws NoUsersException if there are no users in the system
     */
    @Override
    public Stream<UserResponse> findAllUsers() {
        var users = userRepository.findAll();

        if (users.isEmpty()) {
            throw new NoUsersException("No users found");
        }

        return users.stream()
                .map(UserResponse::fromModel);
    }

    /**
     * Finds user by ID
     * <p>
     * This method finds a specific user with ID input. This method is only accessed by the admin.
     * </p>
     *
     * @return UserResponse DTO with ID, username, email, list of folders and files
     * @throws UserNotFoundException if the user doesn't exist
     * @throws IllegalArgumentException if the UUID is invalid*/
    @Override
    public User findUserById(String id) throws GetUserException {

        Optional<User> user = userRepository.findById(UUID.fromString(id));

        if (user.isEmpty()) {
            throw new UserNotFoundException("No user found with id: " + id);
        }

        if (!ValidationUtils.isValidUUID(id)) {
            throw new IllegalArgumentException("Invalid user id: " + id);
        }

        return user.get();
    }

    /**
     * Login request
     * <p>
     * This method attempts to log in the user. It validates the email and password.
     * Then it sends the token so that the user is authenticated and can use other
     * endpoints in this application.
     * </p>
     *
     * @throws InvalidLoginException if the email or password is incorrect
     * @return the token as a String
     */
    @Override
    public String signInUser(LoginUserRequest request) {

        var user = userRepository.findUserByEmail(request.getEmail());

        if (user == null) {
            throw new InvalidLoginException("Invalid email or password");
        }

        assert user.getPassword() != null;
        BCrypt.Result result = BCrypt.verifyer().verify(
                request.getPassword().toCharArray(),
                user.getPassword()
        );

        if (!result.verified) {
            throw new InvalidLoginException("Invalid email or password");
        }

        return jwtService.generateToken(user);
    }

    @Override
    public Optional<User> getUserByOidc(String oidcId, String oidcProvider) {
        return userRepository.findByOidcIdAndOidcProvider(oidcId, oidcProvider);
    }

    @Override
    public User createOidcUser(String email, String oidcId, String oidcProvider) {
        if (userRepository.findUserByEmail(email) != null) {
            throw new UserAlreadyExistsException("User already exists");
        }

        var user = new User();
        user.setEmail(email);
        user.setPassword(null);
        user.setOidcId(oidcId);
        user.setOidcProvider(oidcProvider);
        user.setRole(Role.USER);

        userRepository.save(user);
        log.info("User {} has been created", user.getUsername());

        return user;
    }
}
