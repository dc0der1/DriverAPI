package org.example.droppydriver.service;

import lombok.RequiredArgsConstructor;
import org.example.droppydriver.dtos.CreateFolderRequest;
import org.example.droppydriver.exceptions.folderexceptions.FolderAlreadyExistException;
import org.example.droppydriver.exceptions.userexceptions.InvalidEmailException;
import org.example.droppydriver.exceptions.userexceptions.InvalidPasswordException;
import org.example.droppydriver.models.Folder;
import org.example.droppydriver.models.User;
import org.example.droppydriver.repository.IFolderRepository;
import org.example.droppydriver.repository.IUserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FolderService implements IFolderService {

    private final IFolderRepository folderRepository;
    private final IUserRepository userRepository;

    @Override
    public Folder createFolder(CreateFolderRequest request) {

        if (folderRepository.findFolderByName(request.getFolderName()).isPresent()) {
            throw new FolderAlreadyExistException("Folder name already exists");
        }

        var username = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        User email = userRepository.findUserByEmail(username);

        var folder = new Folder(request.getFolderName(), email);
        folderRepository.save(folder);
        return folder;
    }

}
