package org.example.droppydriver.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.droppydriver.dtos.FileResponse;
import org.example.droppydriver.exceptions.folderexceptions.NoSuchFolderException;
import org.example.droppydriver.service.IFileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import org.example.droppydriver.exceptions.fileexceptions.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;
import java.util.Map;

@RestController
@RequestMapping("/droppydriver/file")
@RequiredArgsConstructor
@Slf4j
public class FileController {

    private final IFileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("Folder") String name, @RequestParam("File") MultipartFile file) {
        try {
            fileService.uploadFile(file, name);
            return ResponseEntity.ok("File uploaded successfully");
        } catch (FileAlreadyExistsException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "File already exists"));
        } catch (NoSuchFolderException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IOException e) {
            log.error(e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "message", "File upload failed"
                    ));
        }
    }

    @GetMapping("/{name}")
    public ResponseEntity<?> getFile(@PathVariable String name) {
        try {
            var file = fileService.findFileByName(name);
            return ResponseEntity.ok(FileResponse.fromModel(file));
        } catch (NoSuchFileException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity
                    .internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

}
