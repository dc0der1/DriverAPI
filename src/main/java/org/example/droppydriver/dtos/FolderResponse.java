package org.example.droppydriver.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.droppydriver.models.FileModel;
import org.example.droppydriver.models.Folder;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class FolderResponse {

    private final UUID id;
    private String folderName;
    private Date createdAt;
    private List<FileModel> files;

    public static FolderResponse fromModel(Folder folder) {
        return new FolderResponse(
                folder.getId(),
                folder.getName(),
                folder.getCreatedAt(),
                folder.getFiles()
        );
    }

}
