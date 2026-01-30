package org.example.droppydriver.service;

import org.example.droppydriver.dtos.CreateFolderRequest;
import org.example.droppydriver.models.Folder;

public interface IFolderService {
    Folder createFolder(CreateFolderRequest folder);
}
