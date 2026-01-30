package org.example.droppydriver.dtos;

import lombok.Getter;
import lombok.Setter;
import org.example.droppydriver.models.Folder;
import org.example.droppydriver.models.User;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Getter
@Setter
public class UserResponse {

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
        return new UserResponse(user.getId(),
                user.getUsername(),
                user.getCreatedAt(),
                user.getEmail(),
                user.getAge(),
                user.getFolders()
                        .stream()
                        .map(FolderResponse::fromModel)
                        .toList()
        );
    }

}
