package org.example.droppydriver.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.droppydriver.exceptions.fileexceptions.FileAlreadyExistsException;
import org.example.droppydriver.exceptions.folderexceptions.NoSuchFolderException;
import org.example.droppydriver.models.FileModel;
import org.example.droppydriver.models.Folder;
import org.example.droppydriver.models.User;
import org.example.droppydriver.repository.IFileRepository;
import org.example.droppydriver.repository.IFolderRepository;
import org.example.droppydriver.repository.IUserRepository;
import org.example.droppydriver.utility.AuthUtility;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FileService implements IFileService {

    private final IFileRepository fileRepository;
    private final IUserRepository userRepository;
    private final IFolderRepository folderRepository;

    /**
     * Uploads a file
     * <p>
     * This method validates the argument, file, folder name before it gets uploaded.
     * The user has to be logged in and authenticated in order for them to upload a file.
     * </p>
     *
     * @param file the type of file e.g. PDF, TXT, PNG, MP4 and etc
     * @param folderName the name of the folder that they want to upload the file to
     * @throws IllegalArgumentException if the user didn't pick a file to upload
     * @throws FileAlreadyExistsException if the file already exists
     * @throws NoSuchFolderException if the given folder name doesn't exist
     */
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

        FileModel fileModel =  new FileModel();
        fileModel.setName(file.getOriginalFilename());
        fileModel.setContentType(file.getContentType());
        fileModel.setSize(file.getSize());
        fileModel.setData(file.getBytes());
        fileModel.setFolder(folder);
        fileModel.setOwner(user);

        fileRepository.save(fileModel);
    }

    /**
     * Finds file by name
     * <p>
     * This method finds the file if the file exists.
     * This method does not find other users files, but it finds the current logged-in user's file.
     * </p>
     *
     * @param fileName the input for the file name
     * @return the file {@link FileModel}
     * @throws NoSuchFileException if the file doesn't exist*/
    @Override
    public FileModel findFileByName(String fileName) throws NoSuchFileException {
        return fileRepository.findFileModelByNameAndUserEmail(fileName, AuthUtility.currentUser())
                .orElseThrow(() -> new NoSuchFileException("File " + fileName + " does not exist"));
    }

    /**
     * Finds all the files for the user
     * <p>
     * This method finds all the files.
     * This method does not find other users files, but it finds the current logged-in user's files.
     * </p>
     *
     * @return list of the files in {@link FileModel}
     * @throws NoSuchFileException if the user has no files
     */
    @Override
    public List<FileModel> findAllFiles() throws NoSuchFileException {
        if (fileRepository.findAllFilesByUserEmail(AuthUtility.currentUser()).isEmpty()) {
            throw new NoSuchFileException("No files were found.");
        }

        return fileRepository.findAllFilesByUserEmail(AuthUtility.currentUser());
    }

    /**
     * Deletes a file with given name
     *
     * @param fileName the input for the file name
     * @throws NoSuchFileException if the desired file doesn't exist
     */
    @Override
    @Transactional
    public void deleteFileByName(String fileName) throws NoSuchFileException {

        FileModel file = fileRepository.findFileModelByNameAndUserEmail(fileName, AuthUtility.currentUser())
                .orElseThrow(() -> new NoSuchFileException("File " + fileName + " does not exist"));

        Folder folder = file.getFolder();

        folder.getFiles().remove(file);

        folderRepository.save(folder);
    }
}
