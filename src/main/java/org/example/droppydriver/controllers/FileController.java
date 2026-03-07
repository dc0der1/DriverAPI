package org.example.droppydriver.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.droppydriver.dtos.FileResponse;
import org.example.droppydriver.exceptions.folder.NoSuchFolderException;
import org.example.droppydriver.models.FileModel;
import org.example.droppydriver.services.IFileService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import org.example.droppydriver.exceptions.file.FileAlreadyExistsException;

import java.net.URI;
import java.nio.file.NoSuchFileException;
import java.util.Map;

@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
@Slf4j
public class FileController {

    private final IFileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("folder") String name, @RequestParam("file") MultipartFile file) throws IOException {
        fileService.uploadFile(file, name);
        return ResponseEntity.created(URI.create("/api/file/upload"))
                .body(Map.of("message", "Successfully uploaded file"));
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<?> downloadFile(@PathVariable String fileName) {
        FileModel fileModel = fileService.findFileByName(fileName);
        ByteArrayResource resource = new ByteArrayResource(fileModel.getData());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileModel.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileModel.getName() + "\"")
                .body(resource);
    }

    @GetMapping("/{fileName}")
    public ResponseEntity<?> getFile(@PathVariable String fileName) {
        var file = fileService.findFileByName(fileName);
        return ResponseEntity.ok(FileResponse.fromModel(file));
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllFiles() {
        var files = fileService.findAllFiles();
        return ResponseEntity.ok().body(files.stream().map(FileResponse::fromModel).toList());
    }

    @DeleteMapping("/{fileName}")
    public ResponseEntity<?> deleteFile(@PathVariable String fileName) {
        fileService.deleteFileByName(fileName);
        return ResponseEntity.status(204).build();
    }
}
