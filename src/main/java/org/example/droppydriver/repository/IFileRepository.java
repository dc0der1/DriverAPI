package org.example.droppydriver.repository;

import org.example.droppydriver.models.FileModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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

}
