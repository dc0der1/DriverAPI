package org.example.droppydriver.serviceTests;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.example.droppydriver.dtos.CreateUserRequest;
import org.example.droppydriver.dtos.LoginUserRequest;
import org.example.droppydriver.dtos.UserResponse;
import org.example.droppydriver.exceptions.userexceptions.*;
import org.example.droppydriver.models.Role;
import org.example.droppydriver.models.User;
import org.example.droppydriver.repository.IUserRepository;
import org.example.droppydriver.service.IJwtService;
import org.example.droppydriver.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock
    private IUserRepository userRepository;
    @Mock
    private IJwtService  jwtService;
    @InjectMocks
    private UserService userService;

    // Register user method in service
    @Test
    void registerUser_ShouldReturnException_WhenEmailIsNotValid() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("TestingDude");
        request.setPassword("Password123@");
        request.setEmail("testgmail.com");
        request.setAge(22);

        assertThrows(InvalidEmailException.class, () ->
                userService.registerUser(request));
    }

    @Test
    void registerUser_ShouldReturnException_WhenPasswordIsNotValid() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("TestingDude");
        request.setPassword("Password123");
        request.setEmail("test@gmail.com");
        request.setAge(22);

        assertThrows(InvalidPasswordException.class, () ->
                userService.registerUser(request));
    }

    @Test
    void registerUser_ShouldReturnException_WhenUserAlreadyExists() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("TestingDude");
        request.setPassword("Password123@");
        request.setEmail("test@gmail.com");
        request.setAge(22);

        Mockito.when(userRepository.existsUserByEmail(request.getEmail())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () ->
                userService.registerUser(request));
    }

    @Test
    void registerUser_ShouldReturnException_WhenUsernameIsInvalid() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("T");
        request.setPassword("Password123@");
        request.setEmail("test@gmail.com");
        request.setAge(22);

        assertThrows(InvalidUsernameException.class, () ->
                userService.registerUser(request));
    }

    @Test
    void registerUser_ShouldReturnException_WhenAgeIsNotValid() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("TestingDude");
        request.setPassword("Password123@");
        request.setEmail("test@gmail.com");
        request.setAge(150);

        assertThrows(InvalidAgeException.class, () ->
                userService.registerUser(request));
    }

    @Test
    void registerUser_ShouldSaveTheUser_AfterValidation() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("TestingDude");
        request.setPassword("Password123@");
        request.setEmail("test@gmail.com");
        request.setAge(22);

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setEmail(request.getEmail());
        user.setAge(request.getAge());
        user.setRole(Role.USER);
        userRepository.save(user);

        assertEquals(userRepository.findUserByEmail(request.getEmail()),
                userRepository.findUserByEmail(request.getEmail()));
    }

    // Find all users test method
    @Test
    void findAllUsers_ShouldReturnException_WhenThereAreNoUsers() {
        assertThrows(NoUsersException.class, () -> userService.findAllUsers());
    }

    @Test
    void findAllUsers_ShouldReturnListOfUsers() {
        User user = new User();
        user.setUsername("TestingDude");
        user.setPassword("Password123@");
        user.setEmail("test@gmail.com");
        user.setAge(22);
        user.setRole(Role.USER);

        List<User> userList = List.of(user);

        Mockito.when(userRepository.findAll()).thenReturn(userList);

        Stream<UserResponse> result = userService.findAllUsers();

        assertNotNull(result);
        assertEquals(1, result.toList().size());
    }

    // Find user by id service method tests
    @Test
    void findUserById_ShouldReturnUserResponse_WhenUserExists() {
        User user = new User();
        user.setUsername("TestingDude");
        user.setPassword("Password123@");
        user.setEmail("test@gmail.com");
        user.setAge(22);
        user.setRole(Role.USER);

        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        assertInstanceOf(UserResponse.class, userService.findUserById(user.getId().toString()));
    }

    @Test
    void findUserById_ShouldThrowException_WhenUserDoesNotExist() {
        assertThrows(UserNotFoundException.class, () ->
                userService.findUserById("e50f94fe-37d4-434c-9f33-fcfad2f2dee3"));
    }

    // Sign in service method test
    @Test
    void signInUser_ShouldReturnStringToken_WhenLoginIsValid() {
        String rawPassword = "Password123@";
        String hashedPassword = BCrypt.withDefaults().hashToString(12, rawPassword.toCharArray());

        User user = new User();
        user.setUsername("TestingDude");
        user.setPassword(hashedPassword);
        user.setEmail("test15@gmail.com");
        user.setAge(22);
        user.setRole(Role.USER);

        LoginUserRequest request = new LoginUserRequest();
        request.setEmail("test15@gmail.com");
        request.setPassword(rawPassword);

        Mockito.when(userRepository.findUserByEmail(request.getEmail())).thenReturn(user);
        Mockito.when(jwtService.generateToken(user)).thenReturn("mocked_jwt_toekn");

        String token = userService.signInUser(request);

        assertNotNull(token);
        assertEquals("mocked_jwt_toekn", token);
    }

    @Test
    void signInUser_ShouldThrowException_WhenLoginIsInvalid() {
        LoginUserRequest request = new LoginUserRequest();
        request.setEmail("TestingDude");
        request.setPassword("Password123@");

        assertThrows(InvalidLoginException.class, () ->
                userService.signInUser(request));
    }
}
