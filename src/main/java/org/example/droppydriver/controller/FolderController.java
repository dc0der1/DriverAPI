package org.example.droppydriver.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.droppydriver.dtos.CreateFolderRequest;
import org.example.droppydriver.dtos.FolderResponse;
import org.example.droppydriver.exceptions.folderexceptions.FolderAlreadyExistException;
import org.example.droppydriver.exceptions.folderexceptions.NoSuchFolderException;
import org.example.droppydriver.exceptions.userexceptions.InvalidEmailException;
import org.example.droppydriver.exceptions.userexceptions.InvalidPasswordException;
import org.example.droppydriver.exceptions.userexceptions.UserNotFoundException;
import org.example.droppydriver.service.IFolderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/droppydriver/folder")
@RequiredArgsConstructor
@Slf4j
public class FolderController {

    private final IFolderService folderService;

    @PostMapping
    public ResponseEntity<?> createFolder(@RequestBody CreateFolderRequest request) {
        try {
            var folder = folderService.createFolder(request);
            return ResponseEntity.created(URI.create("/droppydriver/folder")).body(FolderResponse.fromModel(folder));
        } catch (FolderAlreadyExistException | InvalidPasswordException | UserNotFoundException | InvalidEmailException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "error", e.getMessage()
                    ));
        } catch (Exception e) {
            log.error("Error while creating folder", e);
            return ResponseEntity
                    .badRequest()
                    .build();
        }
    }

    @GetMapping
    public ResponseEntity<?> getFolder(@RequestParam("Folder") String folderName) {
        try {
            var folder = folderService.findFolderByName(folderName);
            return ResponseEntity.ok(FolderResponse.fromModel(folder));
        } catch (NoSuchFolderException e) {
            return ResponseEntity
                    .notFound()
                    .build();
        } catch (Exception e) {
            log.error("Error while getting folder", e);
            return ResponseEntity
                    .internalServerError()
                    .build();
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllFolders() {
        try {
            var folders = folderService.findAllFolders();
            return ResponseEntity.ok().body(folders.stream().map(FolderResponse::fromModel).toList());
        } catch (NoSuchFolderException e) {
            return ResponseEntity
                    .notFound()
                    .build();
        } catch (Exception e) {
            log.error("Error while getting folders", e);
            return ResponseEntity
                    .internalServerError()
                    .build();
        }

    }

}
