package org.example.droppydriver.service;

import org.example.droppydriver.models.FileModel;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.List;

public interface IFileService {

    void uploadFile(MultipartFile file, String folderName) throws IOException;
    FileModel findFileByName(String fileName) throws NoSuchFileException;
    List<FileModel> findAllFiles() throws NoSuchFileException;
    void deleteFileByName(String fileName) throws NoSuchFileException;

}
