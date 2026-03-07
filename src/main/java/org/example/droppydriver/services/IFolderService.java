package org.example.droppydriver.services;

import org.example.droppydriver.dtos.CreateFolderRequest;
import org.example.droppydriver.models.FolderModel;

import java.util.List;

public interface IFolderService {
    FolderModel createFolder(CreateFolderRequest folder);
    FolderModel findFolderByName(String folderName);
    List<FolderModel> findAllFolders();
}
