package org.example.droppydriver.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.droppydriver.dtos.CreateFolderRequest;
import org.example.droppydriver.exceptions.folderexceptions.FolderAlreadyExistException;
import org.example.droppydriver.exceptions.folderexceptions.NoSuchFolderException;
import org.example.droppydriver.exceptions.userexceptions.UserNotFoundException;
import org.example.droppydriver.models.Folder;
import org.example.droppydriver.models.User;
import org.example.droppydriver.repository.IFolderRepository;
import org.example.droppydriver.repository.IUserRepository;
import org.example.droppydriver.utility.AuthUtility;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FolderService implements IFolderService {

    private final IFolderRepository folderRepository;
    private final IUserRepository userRepository;

    /**
    * Create a new folder for the currently authenticated user.
    * <p>
    * This method validates that the folder name is unique within the system
    * before it gets created. It retrieves the current user's details via
    * the security context to establish ownership.
    * </p>
    *
    * @param request the DTO containing the desired folder name
    * @return the newly created and persisted {@link Folder} entity
    * @throws FolderAlreadyExistException if a folder with the given name already exists
    * @throws UserNotFoundException if the authenticated user cannot be found in the database
     * */
    @Override
    @Transactional
    public Folder createFolder(CreateFolderRequest request) {
        if (folderRepository.existsByNameAndUserEmail(AuthUtility.currentUser(), request.getFolderName())) {
            throw new FolderAlreadyExistException("Folder name already exists");
        }

        var user = userRepository.findUserByEmail(AuthUtility.currentUser());

        if (user == null) {
            throw new UserNotFoundException("Authenticated user not found");
        }

        var folder = new Folder(request.getFolderName(), user);
        folderRepository.save(folder);
        return folder;
    }

    /**
     * Find folder by given id
     * <p>
     * This method validates that the folder does exist within the authenticated account.
     * </p>
     *
     * @param folderName the String containing desired name
     * @return the found {@link Folder} entity
     * @throws NoSuchFolderException if the folder is not found in the account
     */
    @Override
    public Folder findFolderByName(String folderName) {
        return folderRepository.findFolderByNameAndUserEmail(folderName, AuthUtility.currentUser())
                .orElseThrow(() -> new NoSuchFolderException("Folder not found"));
    }

    /**
     * Finds all current user folders
     * <p>
     * This method checks if the authenticated user has folders.
     * </p>
     *
     * @return list of folders
     * @throws NoSuchFolderException if no folder was found in current authenticated account*/
    @Override
    public List<Folder> findAllFolders() {
        List<Folder> folders = folderRepository.findAllFoldersByUserEmail(AuthUtility.currentUser());

        if (folders.isEmpty()) {
            throw new NoSuchFolderException("Folder not found");
        }

        return folders;
    }

}
