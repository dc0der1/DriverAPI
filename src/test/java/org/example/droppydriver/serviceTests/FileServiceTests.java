package org.example.droppydriver.serviceTests;

import org.example.droppydriver.dtos.CreateFolderRequest;
import org.example.droppydriver.exceptions.fileexceptions.FileAlreadyExistsException;
import org.example.droppydriver.exceptions.folderexceptions.FolderAlreadyExistException;
import org.example.droppydriver.exceptions.folderexceptions.NoSuchFolderException;
import org.example.droppydriver.exceptions.userexceptions.*;
import org.example.droppydriver.models.FileModel;
import org.example.droppydriver.models.Folder;
import org.example.droppydriver.models.Role;
import org.example.droppydriver.models.User;
import org.example.droppydriver.repository.IFileRepository;
import org.example.droppydriver.repository.IFolderRepository;
import org.example.droppydriver.repository.IUserRepository;
import org.example.droppydriver.service.FileService;
import org.example.droppydriver.service.FolderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class FileServiceTests {

    @Mock
    private IFileRepository fileRepository;
    @Mock
    private IFolderRepository folderRepository;
    @InjectMocks
    private FileService fileService;

    // Upload file method service tests
    @Test
    void uploadFile_ShouldThrowIllegalArgumentException_WhenFileIsEmpty() {
        MockMultipartFile mockFile = new MockMultipartFile("file", "".getBytes());

        assertThrows(IllegalArgumentException.class, () -> fileService.uploadFile(mockFile, "Folder"));
    }

    @Test
    void uploadFile_ShouldThrowFileAlreadyExistException() throws IOException {
        MockMultipartFile mockFile = new MockMultipartFile("file", "some data".getBytes());
        FileModel fileModel = new FileModel();

        Mockito.when(fileRepository.findFileByName(mockFile.getOriginalFilename())).thenReturn(Optional.of(fileModel));
        assertThrows(FileAlreadyExistsException.class, () -> fileService.uploadFile(mockFile, "Folder"));
    }

    @Test
    void uploadFile_ShouldThrowNoSuchFolderException() throws NoSuchFolderException {
        MockMultipartFile mockFile = new MockMultipartFile("file", "some data".getBytes());

        Mockito.when(folderRepository.findFolderByName("Folder")).thenReturn(Optional.empty());
        assertThrows(NoSuchFolderException.class, () -> fileService.uploadFile(mockFile, "Folder"));
    }

    @Test
    void uploadFile_ShouldThrowNoSuchFolderException_WhenFolderDoesNotExistInUsersAccount() {
        MockMultipartFile mockFile = new MockMultipartFile("file", "some data".getBytes());

        //Mockito.when(folderRepository.existsByNameAndUserEmail("test", "Folder")).thenReturn(false);
        assertThrows(NoSuchFolderException.class, () -> fileService.uploadFile(mockFile, "Folder"));
    }

    @Test
    void uploadFile_ShouldSaveFile_AfterValidations() throws IOException {
        MockMultipartFile mockFile = new MockMultipartFile("file", "some data".getBytes());

        Mockito.when(folderRepository.existsByNameAndUserEmail(null, "Folder")).thenReturn(true);
        assertDoesNotThrow(() -> fileService.uploadFile(mockFile, "Folder"));
    }
}
