package org.example.droppydriver.service;

import org.example.droppydriver.dtos.CreateUserRequest;
import org.example.droppydriver.dtos.LoginUserRequest;
import org.example.droppydriver.dtos.UserResponse;
import org.example.droppydriver.models.User;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public interface IUserService {
    User registerUser(CreateUserRequest user);
    Stream<UserResponse> findAllUsers();
    UserResponse findUserById(String id);
    String signInUser(LoginUserRequest request);
}
