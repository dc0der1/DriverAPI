package org.example.droppydriver.services;

import at.favre.lib.crypto.bcrypt.BCrypt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.droppydriver.dtos.CreateUserRequest;
import org.example.droppydriver.dtos.LoginUserRequest;
import org.example.droppydriver.dtos.UserResponse;
import org.example.droppydriver.exceptions.user.*;
import org.example.droppydriver.models.Role;
import org.example.droppydriver.models.UserModel;
import org.example.droppydriver.repositories.IUserRepository;
import org.example.droppydriver.utilities.ValidationUtils;
import org.springframework.stereotype.Service;

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
     * @return the newly registered user {@link UserModel}
     * @throws InvalidEmailException if the email is invalid or doesn't contain this symbol '@'
     * @throws InvalidPasswordException if the password is invalid e.g. doesn't contain number, upper & lowercase or symbol
     * @throws UserAlreadyExistsException if the user email already exists
     * @throws InvalidAgeException if the age input is unrealistic such as 3 years old or 1000 years old
     * @throws InvalidUsernameException if the username is too short
     */
    @Override
    public UserModel registerUser(CreateUserRequest request) throws CreateUserException {
        if (userRepository.existsUserByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("User already exists: " + request.getEmail());
        }

        String hashedPassword = BCrypt.withDefaults().hashToString(12, request.getPassword().toCharArray());

        var user = new UserModel(request.getUsername(), hashedPassword, request.getEmail(), request.getAge(), Role.USER);

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
    public UserModel findUserById(String id) throws GetUserException {

        Optional<UserModel> user = userRepository.findById(UUID.fromString(id));

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

        var optionalUserModel = userRepository.findUserByEmail(request.getEmail());

        if (optionalUserModel.isEmpty()) {
            throw new InvalidLoginException("Invalid email or password");
        }

        var user = optionalUserModel.get();

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
    public Optional<UserModel> getUserByOidc(String oidcId, String oidcProvider) {
        return userRepository.findByOidcIdAndOidcProvider(oidcId, oidcProvider);
    }

    @Override
    public UserModel createOidcUser(String email, String oidcId, String oidcProvider) {
        if (userRepository.findUserByEmail(email) != null) {
            throw new UserAlreadyExistsException("User already exists");
        }

        var user = new UserModel();
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
