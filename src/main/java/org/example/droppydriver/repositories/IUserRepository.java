package org.example.droppydriver.repositories;

import org.example.droppydriver.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IUserRepository extends JpaRepository<UserModel, UUID> {
    Optional<UserModel> findUserByEmail(String email);
    boolean existsUserByEmail(String email);
    Optional<UserModel> findByOidcIdAndOidcProvider(String oidcId, String oidcProvider);
}
