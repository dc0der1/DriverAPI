package org.example.droppydriver.repositories;

import org.example.droppydriver.models.FileModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IFileRepository extends JpaRepository<FileModel, UUID> {
    @Query(value = """
    SELECT files.* FROM files
    INNER JOIN users ON files.owner_id = users.id
    WHERE files.name = ?1 AND users.email = ?2
    """, nativeQuery = true)
    Optional<FileModel> findFileModelByNameAndUserEmail(String fileName, String email);

    @Query(value = """
    SELECT files.* FROM files
    INNER JOIN users ON files.owner_id = users.id
    WHERE users.email = ?1
    """, nativeQuery = true)
    List<FileModel> findAllFilesByUserEmail(String email);
}
