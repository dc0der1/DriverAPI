package org.example.droppydriver.dtos;

import lombok.*;
import org.example.droppydriver.controllers.FolderController;
import org.example.droppydriver.models.FolderModel;
import org.springframework.hateoas.RepresentationModel;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Getter
@Setter
@NoArgsConstructor
public class FolderResponse extends RepresentationModel<@NonNull FolderResponse> {

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

    public static FolderResponse fromModel(FolderModel folderModel) {
        var response = new FolderResponse(
                folderModel.getId(),
                folderModel.getName(),
                folderModel.getCreatedAt(),
                folderModel.getFiles()
                        .stream()
                        .map(FileResponse::fromModel)
                        .toList()
        );

        response.add(linkTo(
                methodOn(FolderController.class).getFolder(folderModel.getName()))
                .withRel("folder")
        );

        return response;
    }

}
