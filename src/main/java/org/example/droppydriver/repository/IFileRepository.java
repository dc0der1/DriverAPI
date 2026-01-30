package org.example.droppydriver.repository;

import org.example.droppydriver.models.FileModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IFileRepository extends JpaRepository<FileModel, UUID> {
}
