package org.example.droppydriver.services;

import org.example.droppydriver.dtos.CreateUserRequest;
import org.example.droppydriver.dtos.LoginUserRequest;
import org.example.droppydriver.dtos.UserResponse;
import org.example.droppydriver.models.UserModel;

import java.util.Optional;
import java.util.stream.Stream;

public interface IUserService {
    UserModel registerUser(CreateUserRequest user);
    Stream<UserResponse> findAllUsers();
    UserModel findUserById(String id);
    String signInUser(LoginUserRequest request);
    Optional<UserModel> getUserByOidc(String oidcId, String oidcProvider);
    UserModel createOidcUser(String email, String oidcId, String oidcProvider);
}
