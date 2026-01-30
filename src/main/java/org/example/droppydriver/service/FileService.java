package org.example.droppydriver.service;

import lombok.RequiredArgsConstructor;
import org.example.droppydriver.models.FileModel;
import org.example.droppydriver.models.User;
import org.example.droppydriver.repository.IFileRepository;
import org.example.droppydriver.repository.IUserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FileService {

    private final IFileRepository fileRepository;
    private final IUserRepository userRepository;

    public void uploadFile(MultipartFile file) throws IOException {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot upload empty file");
        }

        var email = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        User user = userRepository.findUserByEmail(email);

        FileModel fileModel =  new FileModel();
        fileModel.setName(file.getOriginalFilename());
        fileModel.setContentType(file.getContentType());
        fileModel.setSize(file.getSize());
        fileModel.setData(file.getBytes());
        fileModel.setOwner(user);

        fileRepository.save(fileModel);
    }

}
