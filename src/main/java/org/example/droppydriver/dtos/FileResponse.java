package org.example.droppydriver.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.example.droppydriver.models.FileModel;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class FileResponse {

    private UUID id;
    private String contentType;
    private String fileName;
    private Long size;

    public static FileResponse fromModel(FileModel fileModel) {
        return new FileResponse(fileModel.getId(),
                fileModel.getContentType(),
                fileModel.getName(),
                fileModel.getSize());
    }
}
