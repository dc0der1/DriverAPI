package org.example.droppydriver.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.example.droppydriver.controller.FileController;
import org.example.droppydriver.models.FileModel;
import org.springframework.hateoas.RepresentationModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class FileResponse extends RepresentationModel<@NonNull FileResponse> {

    private UUID id;
    private String contentType;
    private String fileName;
    private Long size;

    public static FileResponse fromModel(FileModel fileModel) {
        var response = new FileResponse(fileModel.getId(),
                fileModel.getContentType(),
                fileModel.getName(),
                fileModel.getSize());

        response.add(linkTo(
                methodOn(FileController.class).getFile(fileModel.getName()))
                .withRel("file")
        );

        return response;
    }
}
