package org.example.droppydriver.repository;

import org.example.droppydriver.models.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IFolderRepository extends JpaRepository<Folder, UUID> {
    Optional<Folder> findFolderByName(String name);
    Optional<Folder> findFolderByIdAndUserEmail(UUID id, String email);
    List<Folder> findAllFoldersByUserEmail(String email);

    @Query(value = """
    SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END 
    FROM users u 
    INNER JOIN folders f ON f.user_id = u.id 
    WHERE u.email = ?1 AND f.name = ?2
    """, nativeQuery = true)
    boolean existsByNameAndUserEmail(String email, String folderName);
    Optional<Folder> findFolderByNameAndUserEmail(String name, String email);
}
