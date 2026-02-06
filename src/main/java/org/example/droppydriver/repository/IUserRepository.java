package org.example.droppydriver.repository;

import org.example.droppydriver.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IUserRepository extends JpaRepository<User, UUID> {
    User findUserByEmail(String email);
    Optional<User> findUserByPassword(String password);
    User findUserByEmailAndPassword(String email, String password);
    boolean existsUserByEmail(String email);
}
