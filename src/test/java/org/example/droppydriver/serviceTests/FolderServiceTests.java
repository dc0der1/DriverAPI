package org.example.droppydriver.serviceTests;

import org.example.droppydriver.dtos.CreateFolderRequest;
import org.example.droppydriver.exceptions.folderexceptions.FolderAlreadyExistException;
import org.example.droppydriver.exceptions.folderexceptions.NoSuchFolderException;
import org.example.droppydriver.exceptions.userexceptions.*;
import org.example.droppydriver.models.Folder;
import org.example.droppydriver.models.Role;
import org.example.droppydriver.models.User;
import org.example.droppydriver.repository.IFolderRepository;
import org.example.droppydriver.repository.IUserRepository;
import org.example.droppydriver.service.FolderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class FolderServiceTests {

    @Mock
    private IUserRepository userRepository;
    @Mock
    private IFolderRepository folderRepository;
    @InjectMocks
    private FolderService folderService;

    // Create folder service method tests
    @Test
    void createFolderTest_ShouldThrowException_WhenFolderAlreadyExists() {
        CreateFolderRequest request = new CreateFolderRequest();
        request.setFolderName("Folder");

        Folder folder = new Folder();
        folder.setName(request.getFolderName());

        Mockito.when(folderRepository.findFolderByName(request.getFolderName())).thenReturn(Optional.of(folder));

        assertThrows(FolderAlreadyExistException.class, () -> folderService.createFolder(request));
    }

    @Test
    void createFolderTest_ShouldThrowException_WhenUserIsNotFound() {
        CreateFolderRequest request = new CreateFolderRequest();
        request.setFolderName("Folder");

        assertThrows(UserNotFoundException.class, () -> folderService.createFolder(request));
    }

    @Test
    void createFolderTest_ShouldSaveFolder_AfterValidation() {
        CreateFolderRequest request = new CreateFolderRequest();
        request.setFolderName("Folder");

        Folder folder = new Folder();
        folder.setName(request.getFolderName());

        User user = new User();
        user.setUsername("TestingDude");
        user.setPassword("Password123@");
        user.setEmail("test@gmail.com");
        user.setAge(22);
        user.setRole(Role.USER);

        Mockito.when(userRepository.findUserByEmail(null)).thenReturn(user);

        assertInstanceOf(Folder.class, folderService.createFolder(request));
    }

    // Find folder by id service method tests
    @Test
    void findFolderByIdTest_ShouldThrowException_WhenFolderNotFound() {
        assertThrows(NoSuchFolderException.class, () -> folderService.findFolderById(UUID.randomUUID()));
    }

    @Test
    void findFolderByIdTest_ShouldReturnFolder_AfterValidation() {
        Folder folder = new Folder();
        folder.setName("Folder");

        Mockito.when(folderRepository.findFolderByIdAndUserEmail(folder.getId(), null)).thenReturn(Optional.of(folder));
        assertInstanceOf(Folder.class, folderService.findFolderById(folder.getId()));
    }

    // Find all folders service method tests
    @Test
    void findAllFolders_ShouldReturnListOfFolders() {
        Mockito.when(folderRepository.findAllFoldersByUserEmail(null))
                .thenReturn(List.of(new Folder()));
        assertInstanceOf(List.class, folderService.findAllFolders());
    }

    @Test
    void findAllFolders_ShouldThrowException_WhenFoldersAreNotFound() {
        assertThrows(NoSuchFolderException.class, () -> folderService.findAllFolders());
    }
}
