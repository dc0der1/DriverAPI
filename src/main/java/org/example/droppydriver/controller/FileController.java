package org.example.droppydriver.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.droppydriver.dtos.FileResponse;
import org.example.droppydriver.exceptions.folderexceptions.NoSuchFolderException;
import org.example.droppydriver.models.FileModel;
import org.example.droppydriver.service.IFileService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
            return ResponseEntity.ok(Map.of("message", "Successfully uploaded file"));
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

    @GetMapping("/download/{fileName}")
    public ResponseEntity<?> downloadFile(@PathVariable String fileName) {
        try {
            FileModel fileModel = fileService.findFileByName(fileName);
            ByteArrayResource resource = new ByteArrayResource(fileModel.getData());

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(fileModel.getContentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileModel.getName() + "\"")
                    .body(resource);
        } catch (NoSuchFileException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{fileName}")
    public ResponseEntity<?> getFile(@PathVariable String fileName) {
        try {
            var file = fileService.findFileByName(fileName);
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

    @GetMapping("/all")
    public ResponseEntity<?> getAllFiles() {
        try {
            var files = fileService.findAllFiles();
            return ResponseEntity.ok().body(files.stream().map(FileResponse::fromModel).toList());
        } catch (NoSuchFileException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity
                    .internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{fileName}")
    public ResponseEntity<?> deleteFile(@PathVariable String fileName) {
        try {
            fileService.deleteFileByName(fileName);
            return ResponseEntity.status(204).build();
        } catch (NoSuchFileException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity
                    .internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }


}
