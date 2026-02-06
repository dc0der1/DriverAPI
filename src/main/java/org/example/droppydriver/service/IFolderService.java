package org.example.droppydriver.service;

import org.example.droppydriver.dtos.CreateFolderRequest;
import org.example.droppydriver.models.Folder;

import java.util.List;
import java.util.UUID;

public interface IFolderService {
    Folder createFolder(CreateFolderRequest folder);
    Folder findFolderById(UUID folder_id);
    List<Folder> findAllFolders();
}
