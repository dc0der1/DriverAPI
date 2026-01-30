package org.example.droppydriver.repository;

import org.example.droppydriver.models.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IFolderRepository extends JpaRepository<Folder, UUID> {
    Optional<Folder> findFolderByName(String name);
}
