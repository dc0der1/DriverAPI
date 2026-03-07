package org.example.droppydriver.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.droppydriver.dtos.CreateFolderRequest;
import org.example.droppydriver.exceptions.folder.FolderAlreadyExistException;
import org.example.droppydriver.exceptions.folder.NoSuchFolderException;
import org.example.droppydriver.exceptions.user.UserNotFoundException;
import org.example.droppydriver.models.FolderModel;
import org.example.droppydriver.repositories.IFolderRepository;
import org.example.droppydriver.repositories.IUserRepository;
import org.example.droppydriver.utilities.AuthUtility;
import org.springframework.stereotype.Service;

import java.util.List;

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
    * @return the newly created and persisted {@link FolderModel} entity
    * @throws FolderAlreadyExistException if a folder with the given name already exists
    * @throws UserNotFoundException if the authenticated user cannot be found in the database
     * */
    @Override
    @Transactional
    public FolderModel createFolder(CreateFolderRequest request) {
        if (folderRepository.existsByNameAndUserEmail(AuthUtility.currentUser(), request.getFolderName())) {
            throw new FolderAlreadyExistException("Folder name already exists");
        }

        var user = userRepository.findUserByEmail(AuthUtility.currentUser());

        if (user.isEmpty()) {
            throw new UserNotFoundException("Authenticated user not found");
        }

        var folder = new FolderModel(request.getFolderName(), user.get());
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
     * @return the found {@link FolderModel} entity
     * @throws NoSuchFolderException if the folder is not found in the account
     */
    @Override
    public FolderModel findFolderByName(String folderName) {
        return folderRepository.findFolderByNameAndUserModelEmail(folderName, AuthUtility.currentUser())
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
    public List<FolderModel> findAllFolders() {
        List<FolderModel> folderModels = folderRepository.findAllFoldersByUserModelEmail(AuthUtility.currentUser());

        if (folderModels.isEmpty()) {
            throw new NoSuchFolderException("Folder not found");
        }

        return folderModels;
    }

}
