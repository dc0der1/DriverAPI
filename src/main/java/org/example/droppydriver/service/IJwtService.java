package org.example.droppydriver.service;

import org.example.droppydriver.models.User;

import java.util.UUID;

public interface IJwtService {

    String generateToken(User user);
    UUID validateToken(String token);

}
