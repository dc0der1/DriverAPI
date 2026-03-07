package org.example.droppydriver.services;

import org.example.droppydriver.models.UserModel;

import java.util.UUID;

public interface IJwtService {

    String generateToken(UserModel userModel);
    UUID validateToken(String token);

}
