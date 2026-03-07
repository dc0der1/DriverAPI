package org.example.droppydriver.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.droppydriver.dtos.CreateFolderRequest;
import org.example.droppydriver.dtos.FolderResponse;
import org.example.droppydriver.exceptions.folder.FolderAlreadyExistException;
import org.example.droppydriver.exceptions.folder.NoSuchFolderException;
import org.example.droppydriver.exceptions.user.InvalidEmailException;
import org.example.droppydriver.exceptions.user.InvalidPasswordException;
import org.example.droppydriver.exceptions.user.UserNotFoundException;
import org.example.droppydriver.services.IFolderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/folder")
@RequiredArgsConstructor
@Slf4j
public class FolderController {

    private final IFolderService folderService;

    @PostMapping
    public ResponseEntity<?> createFolder(@RequestBody @Valid CreateFolderRequest request) {
        var folder = folderService.createFolder(request);
        return ResponseEntity.created(URI.create("/api/folder")).body(FolderResponse.fromModel(folder));
    }

    @GetMapping
    public ResponseEntity<?> getFolder(@RequestParam("folder") String folderName) {
        var folder = folderService.findFolderByName(folderName);
        return ResponseEntity.ok(FolderResponse.fromModel(folder));
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllFolders() {
        var folders = folderService.findAllFolders();
        return ResponseEntity.ok().body(folders.stream().map(FolderResponse::fromModel).toList());
    }

}
