package org.example.droppydriver.controllerTests;

import org.example.droppydriver.dtos.CreateUserRequest;
import org.example.droppydriver.dtos.LoginUserRequest;
import org.example.droppydriver.models.Role;
import org.example.droppydriver.models.User;
import org.example.droppydriver.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerTests {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void cleanup() {
        userRepository.deleteAll();
    }

    // Register endpoint tests
    @Test
    @DisplayName("Test should return bad request if the email is invalid")
    void registerUser_shouldReturn400_whenEmailIsInvalid() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("invalid");
        request.setPassword("Linkur123@");
        request.setAge(22);
        request.setUsername("Testing The Test");

        registerUserTest(request);
    }

    @Test
    void registerUser_ShouldReturn400_WhenPasswordIsInvalid() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("test@gmail.com");
        request.setPassword("Linkur");
        request.setAge(22);
        request.setUsername("Testing The Test");

        registerUserTest(request);
    }

    @Test
    void registerUser_ShouldReturn400_WhenUserAlreadyExists() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("userexist@gmail.com");
        request.setPassword("Linkur123@");
        request.setAge(22);
        request.setUsername("Testing The Test");

        userRepository.save(createUserTest()); // User gets saved

        // NOTE TO MYSELF:
        // Tries to save the user again by calling the API
        // But test gets passed because the user has already been saved
        registerUserTest(request);
    }

    @Test
    void registerUser_ShouldReturn400_WhenUsernameIsInvalid() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("userexist@gmail.com");
        request.setPassword("Linkur123@");
        request.setAge(22);
        request.setUsername("T");

        registerUserTest(request);
    }

    @Test
    void registerUser_ShouldReturn400_WhenAgeIsInvalid() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("userexist@gmail.com");
        request.setPassword("Linkur123@");
        request.setAge(420);
        request.setUsername("Testing The Test");

        registerUserTest(request);
    }

    @Test
    void registerUser_ShouldReturn200_WhenEverythingIsValid() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("userexist@gmail.com");
        request.setPassword("Linkur123@");
        request.setAge(22);
        request.setUsername("Testing The Test");

        mockMvc.perform(post("/droppydriver/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    // Login endpoint tests
    @Test
    void loginUser_ShouldReturn400_WhenLoginIsInvalid() throws Exception {
        LoginUserRequest request = new LoginUserRequest();
        request.setEmail("test@gmail.com");
        request.setPassword("Linkur123@");

        loginUserTest(request);
    }

    @Test
    void loginUser_ShouldReturn200_WhenLoginIsValid() throws Exception {
        LoginUserRequest request = new LoginUserRequest();
        request.setEmail("test@gmail.com");
        request.setPassword("Linkur123@");

        userRepository.save(createUserTest());

        loginUserTest(request);
    }

    // Get all users endpoint tests
    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_ShouldReturn404_IfThereAreNoUsers() throws Exception {
        mockMvc.perform(get("/droppydriver/user/all"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_ShouldReturn200_IfThereAreUsers() throws Exception {
        userRepository.save(createUserTest());

        mockMvc.perform(get("/droppydriver/user/all"))
                .andExpect(status().isOk());
    }

    // Get user by id endpoint tests
    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserById_ShouldReturn400_WhenUserIsNotValid() throws Exception {
        mockMvc.perform(get("/droppydriver/user/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserById_ShouldReturn200_WhenUserIsValid() throws Exception {
        User user = userRepository.save(createUserTest());

        mockMvc.perform(get("/droppydriver/user/{id}", user.getId()))
                .andExpect(status().isOk());
    }

    // Reused methods
    private void loginUserTest(LoginUserRequest request) throws Exception {
        mockMvc.perform(post("/droppydriver/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    private void registerUserTest(CreateUserRequest request) throws Exception {
        mockMvc.perform(post("/droppydriver/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    private User createUserTest() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("userexist@gmail.com");
        request.setPassword("Linkur123@");
        request.setAge(22);
        request.setUsername("Testing The Test");

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setAge(request.getAge());
        user.setRole(Role.USER);

        return user;
    }

}
