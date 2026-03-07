package org.example.droppydriver.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateFileRequest {

    private String fileName;
    private String folderName;

}
