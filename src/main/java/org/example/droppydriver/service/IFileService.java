package org.example.droppydriver.service;

import org.example.droppydriver.models.FileModel;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.NoSuchFileException;

public interface IFileService {

    void uploadFile(MultipartFile file, String folderName) throws IOException;
    FileModel findFileByName(String fileName) throws NoSuchFileException;

}
