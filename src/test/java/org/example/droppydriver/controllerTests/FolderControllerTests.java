package org.example.droppydriver.controllerTests;

import org.example.droppydriver.dtos.CreateFolderRequest;
import org.example.droppydriver.models.Folder;
import org.example.droppydriver.models.Role;
import org.example.droppydriver.models.User;
import org.example.droppydriver.repository.IFolderRepository;
import org.example.droppydriver.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class FolderControllerTests {

    @Autowired
    private IFolderRepository folderRepository;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void cleanUp() {
        folderRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    // Create folder endpoint tests
    @Test
    @WithMockUser(username = "test@gmail.com")
    void createFolderTest_ShouldReturn400_WhenFolderAlreadyExists() throws Exception {
        CreateFolderRequest request = new CreateFolderRequest();
        request.setFolderName("Folder");

        User user = createUserTest();
        createFolderTest(request.getFolderName(), user);

        mockMvc.perform(post("/droppydriver/folder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void createFolderTest_ShouldReturn201_WhenFolderIsCreated() throws Exception {
        createUserTest();

        CreateFolderRequest request = new CreateFolderRequest();
        request.setFolderName("MyNewFolder");

        mockMvc.perform(post("/droppydriver/folder")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    // Get folder by id tests
    @Test
    @WithMockUser(username = "test@gmail.com")
    void getFolderTest_ShouldReturn404_WhenFolderDoesNotExist() throws Exception {
        createUserTest();

        mockMvc.perform(get("/droppydriver/folder/"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void getFolderTest_ShouldReturn200_WhenFolderIsFound() throws Exception {
        User user = createUserTest();
        Folder folder = createFolderTest(user);

        mockMvc.perform(get("/droppydriver/folder/{id}", folder.getId()))
                .andExpect(status().isOk());
    }

    // Get all folders endpoint test
    @Test
    @WithMockUser(username = "test@gmail.com")
    void getAllFoldersTest_ShouldReturn404_WhenFoldersDoesNotExist()  throws Exception {
        createUserTest();

        mockMvc.perform(get("/droppydriver/folder/all")).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void getAllFoldersTest_ShouldReturn200_WhenFoldersAreFound() throws Exception {
        User user = createUserTest();
        createFolderTest(user);

        mockMvc.perform(get("/droppydriver/folder/all"))
                .andExpect(status().isOk());
    }

    // Reused methods
    private User createUserTest() {
        User user = new User();
        user.setEmail("test@gmail.com");
        user.setPassword("Testing123@");
        user.setUsername("Test User");
        user.setAge(22);
        user.setRole(Role.USER);
        userRepository.save(user);
        return user;
    }

    private void createFolderTest(String folderName, User user) {
        Folder folder = new Folder();
        folder.setName(folderName);
        folder.setUser(user);
        folderRepository.save(folder);
    }

    // Overload
    private Folder createFolderTest(User user) {
        Folder folder = new Folder();
        folder.setName("Folder test");
        folder.setUser(user);
        folderRepository.save(folder);
        return folder;
    }

}
