package org.example.droppydriver.repository;

import org.example.droppydriver.models.FileModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.nio.file.NoSuchFileException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IFileRepository extends JpaRepository<FileModel, UUID> {

    Optional<FileModel>findFileByName(String fileName);

    @Query(value = """
    SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END 
    FROM users u 
    INNER JOIN folders f ON f.user_id = u.id 
    WHERE u.email = ?1 AND f.name = ?2
    """, nativeQuery = true)
    boolean existsByNameAndUserEmail(String email, String folderName);

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
