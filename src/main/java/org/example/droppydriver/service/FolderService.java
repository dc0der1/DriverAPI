package org.example.droppydriver.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.droppydriver.dtos.CreateFolderRequest;
import org.example.droppydriver.exceptions.folderexceptions.FolderAlreadyExistException;
import org.example.droppydriver.exceptions.folderexceptions.NoSuchFolderException;
import org.example.droppydriver.exceptions.userexceptions.InvalidEmailException;
import org.example.droppydriver.exceptions.userexceptions.InvalidPasswordException;
import org.example.droppydriver.models.Folder;
import org.example.droppydriver.models.User;
import org.example.droppydriver.repository.IFolderRepository;
import org.example.droppydriver.repository.IUserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.beans.Transient;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FolderService implements IFolderService {

    private final IFolderRepository folderRepository;
    private final IUserRepository userRepository;

    @Override
    @Transactional
    public Folder createFolder(CreateFolderRequest request) {

        if (folderRepository.findFolderByName(request.getFolderName()).isPresent()) {
            throw new FolderAlreadyExistException("Folder name already exists");
        }

        String email = "";
        var auth = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication());
        if (auth.getPrincipal() instanceof User userDetails) {
            email = userDetails.getEmail();
        }

        User user = userRepository.findUserByEmail(email);

        var folder = new Folder(request.getFolderName(), user);
        folderRepository.save(folder);
        return folder;
    }

    @Override
    public Folder findFolderByName(String folderName) {

        if (folderRepository.findFolderByName(folderName).isEmpty()) {
            throw new NoSuchFolderException("Folder not found");
        }

        return folderRepository.findFolderByName(folderName).get();
    }

}
