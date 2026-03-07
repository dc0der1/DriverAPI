package org.example.droppydriver.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.droppydriver.exceptions.file.FileAlreadyExistsException;
import org.example.droppydriver.exceptions.file.FileNotFoundException;
import org.example.droppydriver.exceptions.folder.NoSuchFolderException;
import org.example.droppydriver.exceptions.user.UserNotFoundException;
import org.example.droppydriver.models.FileModel;
import org.example.droppydriver.models.FolderModel;
import org.example.droppydriver.models.UserModel;
import org.example.droppydriver.repositories.IFileRepository;
import org.example.droppydriver.repositories.IFolderRepository;
import org.example.droppydriver.repositories.IUserRepository;
import org.example.droppydriver.utilities.AuthUtility;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

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

        if (fileRepository.findFileModelByNameAndUserEmail(file.getOriginalFilename() ,AuthUtility.currentUser()).isPresent()) {
            throw new FileAlreadyExistsException("File already exists");
        }

        if (!folderRepository.existsByNameAndUserEmail(AuthUtility.currentUser(), folderName)) {
            throw new NoSuchFolderException("Folder " + folderName + " does not exist on this account");
        }

        if (userRepository.findUserByEmail(AuthUtility.currentUser()).isEmpty()) {
            throw new UserNotFoundException("Authenticated user not found");
        }

        UserModel userModel = userRepository.findUserByEmail(AuthUtility.currentUser()).get();

        FolderModel folderModel = folderRepository.findFolderByNameAndUserModelEmail(folderName, AuthUtility.currentUser()).get();

        FileModel fileModel =  new FileModel();
        fileModel.setName(file.getOriginalFilename());
        fileModel.setContentType(file.getContentType());
        fileModel.setSize(file.getSize());
        fileModel.setData(file.getBytes());
        fileModel.setFolderModel(folderModel);
        fileModel.setOwner(userModel);

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
     * @throws FileNotFoundException if the file doesn't exist*/
    @Override
    public FileModel findFileByName(String fileName) {
        return fileRepository.findFileModelByNameAndUserEmail(fileName, AuthUtility.currentUser())
                .orElseThrow(() -> new FileNotFoundException("File doesn't exist"));
    }

    /**
     * Finds all the files for the user
     * <p>
     * This method finds all the files.
     * This method does not find other users files, but it finds the current logged-in user's files.
     * </p>
     *
     * @return list of the files in {@link FileModel}
     * @throws FileNotFoundException if the user has no files
     */
    @Override
    public List<FileModel> findAllFiles() {
        if (fileRepository.findAllFilesByUserEmail(AuthUtility.currentUser()).isEmpty()) {
            throw new FileNotFoundException("No files were found.");
        }

        return fileRepository.findAllFilesByUserEmail(AuthUtility.currentUser());
    }

    /**
     * Deletes a file with given name
     *
     * @param fileName the input for the file name
     * @throws FileNotFoundException if the desired file doesn't exist
     */
    @Override
    @Transactional
    public void deleteFileByName(String fileName) {

        FileModel file = fileRepository.findFileModelByNameAndUserEmail(fileName, AuthUtility.currentUser())
                .orElseThrow(() -> new FileNotFoundException("File " + fileName + " does not exist"));

        FolderModel folderModel = file.getFolderModel();

        folderModel.getFiles().remove(file);

        folderRepository.save(folderModel);
    }
}
