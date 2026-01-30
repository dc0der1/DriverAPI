package org.example.droppydriver.service;

import lombok.RequiredArgsConstructor;
import org.example.droppydriver.exceptions.fileexceptions.FileAlreadyExistsException;
import org.example.droppydriver.exceptions.folderexceptions.NoSuchFolderException;
import org.example.droppydriver.models.FileModel;
import org.example.droppydriver.models.Folder;
import org.example.droppydriver.models.User;
import org.example.droppydriver.repository.IFileRepository;
import org.example.droppydriver.repository.IFolderRepository;
import org.example.droppydriver.repository.IUserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FileService implements IFileService {

    private final IFileRepository fileRepository;
    private final IUserRepository userRepository;
    private final IFolderRepository folderRepository;

    public void uploadFile(MultipartFile file, String folderName) throws IOException {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot upload empty file");
        }

        if (fileRepository.findFileByName(file.getOriginalFilename()).isPresent()) {
            throw new FileAlreadyExistsException("File already exists");
        }

        if (folderRepository.findFolderByName(folderName).isEmpty()) {
            throw new NoSuchFolderException("Folder " + folderName + " does not exist");
        }

        String email = "";
        var auth = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication());
        if (auth.getPrincipal() instanceof User userDetails) {
            email = userDetails.getEmail();
        }

        if (!fileRepository.existsByNameAndUserEmail(email, folderName)) {
            throw new NoSuchFolderException("Folder " + folderName + " does not exist on this account");
        }

        User user = userRepository.findUserByEmail(email);

        Folder folder = folderRepository.findFolderByName(folderName).get();

        System.out.println(folder.getName());

        FileModel fileModel =  new FileModel();
        fileModel.setName(file.getOriginalFilename());
        fileModel.setContentType(file.getContentType());
        fileModel.setSize(file.getSize());
        fileModel.setData(file.getBytes());
        fileModel.setFolder(folder);
        fileModel.setOwner(user);

        fileRepository.save(fileModel);
    }

    @Override
    public FileModel findFileByName(String fileName) throws NoSuchFileException {

        if (fileRepository.findFileByName(fileName).isEmpty()) {
            throw new NoSuchFileException("File " + fileName + " does not exist");
        }

        return fileRepository.findFileByName(fileName).get();
    }
}
