package org.example.droppydriver.controllerTests;

import org.example.droppydriver.models.FileModel;
import org.example.droppydriver.models.Folder;
import org.example.droppydriver.models.Role;
import org.example.droppydriver.models.User;
import org.example.droppydriver.repository.IFileRepository;
import org.example.droppydriver.repository.IFolderRepository;
import org.example.droppydriver.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class FileControllerTests {

    @Autowired
    private IFileRepository fileRepository;

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
        fileRepository.deleteAllInBatch();
        folderRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    // Upload endpoint tests
    @Test
    @WithMockUser(username = "test@gmail.com")
    void uploadFile_ShouldReturn400_WhenFileAlreadyExists() throws Exception {
        User user = createUserTest();
        Folder folder = createFolderTest(user);
        MockMultipartFile mockFile = createFileTest();
        createFileModelTest(mockFile, folder, user);

        mockMvc.perform(multipart("/droppydriver/file/upload")
                .file(mockFile)
                .param("Folder", folder.getName()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void uploadFile_ShouldReturn400_WhenFolderDoesNotExist() throws Exception {
        createUserTest();
        MockMultipartFile mockFile = createFileTest();

        mockMvc.perform(multipart("/droppydriver/file/upload")
                .file(mockFile)
                .param("Folder", "NonExistFolder"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void uploadFile_ShouldReturn200_WhenFileIsUploaded() throws Exception {
        User user = createUserTest();
        Folder folder = createFolderTest(user);

        mockMvc.perform(multipart("/droppydriver/file/upload")
                .file(createFileTest())
                .param("Folder", folder.getName()))
                .andExpect(status().isOk());
    }

    // Download file endpoint tests
    @Test
    @WithMockUser(username = "test@gmail.com")
    void downloadFile_ShouldReturn404_WhenFileDoesNotExist() throws Exception {
        createUserTest();

        mockMvc.perform(get("/droppydriver/file/download/{fileName}", "nonExistFile"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void downloadFile_ShouldReturn200_WhenFileIsDownloaded() throws Exception {
        User user = createUserTest();
        Folder folder = createFolderTest(user);
        MockMultipartFile mockFile = createFileTest();
        createFileModelTest(mockFile, folder, user);

        mockMvc.perform(get("/droppydriver/file/download/{fileName}", mockFile.getOriginalFilename()))
                .andExpect(status().isOk());
    }

    // Get file by name endpoint tests
    @Test
    @WithMockUser(username = "test@gmail.com")
    void getFile_ShouldReturn400_WhenFileDoesNotExist() throws Exception {
        createUserTest();

        mockMvc.perform(get("/droppydriver/file/{fileName}", "nonExistFile"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void getFile_ShouldReturn200_WhenFileExists() throws Exception {
        User user = createUserTest();
        Folder folder = createFolderTest(user);
        MockMultipartFile mockFile = createFileTest();
        createFileModelTest(mockFile, folder, user);

        mockMvc.perform(get("/droppydriver/file/{fileName}", mockFile.getOriginalFilename()))
                .andExpect(status().isOk());
    }

    // Get all files endpoint tests
    @Test
    @WithMockUser(username = "test@gmail.com")
    void getAllFiles_ShouldReturn404_WhenFilesDoesNotExist() throws Exception {
        createUserTest();

        mockMvc.perform(get("/droppydriver/file/all"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void getAllFiles_ShouldReturn200_WhenFilesExist() throws Exception {
        User user = createUserTest();
        Folder folder = createFolderTest(user);
        MockMultipartFile mockFile = createFileTest();
        createFileModelTest(mockFile, folder, user);

        mockMvc.perform(get("/droppydriver/file/all"))
                .andExpect(status().isOk());
    }

    // Delete file endpoint tests
    @Test
    @WithMockUser(username = "test@gmail.com")
    void deleteFile_ShouldReturn404_WhenFileDoesNotExist() throws Exception {
        createUserTest();

        mockMvc.perform(delete("/droppydriver/file/delete/{fileName}", "nonExistFile"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void deleteFile_ShouldReturn204_WhenFileIsDeleted() throws Exception {
        User user = createUserTest();
        Folder folder = createFolderTest(user);
        MockMultipartFile mockFile = createFileTest();
        createFileModelTest(mockFile, folder, user);

        mockMvc.perform(delete("/droppydriver/file/{fileName}", mockFile.getOriginalFilename()))
                .andExpect(status().isNoContent());
    }

    // Reused methods
    private User createUserTest() {
        User user = new User();
        user.setEmail("test@gmail.com");
        user.setPassword("Testing123@");
        user.setUsername("Test User");
        user.setAge(22);
        user.setRole(Role.USER);
        return userRepository.saveAndFlush(user);
    }

    private Folder createFolderTest(User user) {
        Folder folder = new Folder();
        folder.setName("Folder test");
        folder.setUser(user);
        return folderRepository.saveAndFlush(folder);
    }

    private MockMultipartFile createFileTest() throws Exception {
        return new MockMultipartFile(
                "File",
                "test.txt",
                "text/plain",
                "some data".getBytes()
        );
    }

    private FileModel createFileModelTest(MockMultipartFile file, Folder folder, User user) throws Exception {
        FileModel fileModel = new FileModel();
        fileModel.setName(file.getOriginalFilename());
        fileModel.setContentType(file.getContentType());
        fileModel.setSize(file.getSize());
        fileModel.setData(file.getBytes());
        fileModel.setFolder(folder);
        fileModel.setOwner(user);
        return fileRepository.saveAndFlush(fileModel);
    }

}
