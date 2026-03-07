package org.example.droppydriver.repositories;

import org.example.droppydriver.models.FolderModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IFolderRepository extends JpaRepository<FolderModel, UUID> {
    List<FolderModel> findAllFoldersByUserModelEmail(String email);

    @Query(value = """
    SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END 
    FROM users u 
    INNER JOIN folders f ON f.user_id = u.id 
    WHERE u.email = ?1 AND f.name = ?2
    """, nativeQuery = true)
    boolean existsByNameAndUserEmail(String email, String folderName);
    Optional<FolderModel> findFolderByNameAndUserModelEmail(String name, String email);
}
