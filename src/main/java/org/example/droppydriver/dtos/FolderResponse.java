package org.example.droppydriver.dtos;

import lombok.*;
import org.example.droppydriver.models.Folder;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class FolderResponse {

    private UUID id;
    private String folderName;
    private Date createdAt;
    private List<FileResponse> files;

    public FolderResponse(UUID id, String folderName, Date createdAt, List<FileResponse> files) {
        this.id = id;
        this.folderName = folderName;
        this.createdAt = createdAt;
        this.files = files;
    }

    public static FolderResponse fromModel(Folder folder) {
        return new FolderResponse(
                folder.getId(),
                folder.getName(),
                folder.getCreatedAt(),
                folder.getFiles()
                        .stream()
                        .map(FileResponse::fromModel)
                        .toList()
        );
    }

}
