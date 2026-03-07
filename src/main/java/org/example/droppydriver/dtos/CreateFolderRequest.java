package org.example.droppydriver.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class CreateFolderRequest {

    @NotBlank
    @Size(min = 4, max = 512)
    private String folderName;

}
