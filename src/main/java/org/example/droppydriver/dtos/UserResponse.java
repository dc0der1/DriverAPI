package org.example.droppydriver.dtos;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.example.droppydriver.controller.UserController;
import org.example.droppydriver.models.User;
import org.springframework.hateoas.RepresentationModel;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Getter
@Setter
public class UserResponse extends RepresentationModel<@NonNull UserResponse> {

    private final UUID id;
    private String username;
    private Date createdAt;
    private String email;
    private int age;
    private List<FolderResponse> folders;

    public UserResponse(UUID id, String username, Date createdAt, String email, int age,  List<FolderResponse> folders) {
        this.id = id;
        this.username = username;
        this.createdAt = createdAt;
        this.email = email;
        this.age = age;
        this.folders = folders;
    }

    public static UserResponse fromModel(User user) {
        var response = new UserResponse(user.getId(),
                user.getUsername(),
                user.getCreatedAt(),
                user.getEmail(),
                user.getAge(),
                user.getFolders()
                        .stream()
                        .map(FolderResponse::fromModel)
                        .toList()
        );

        response.add(linkTo(
                methodOn(UserController.class).getUserById(user.getId()))
                .withRel("user")
        );

        return response;
    }

}
