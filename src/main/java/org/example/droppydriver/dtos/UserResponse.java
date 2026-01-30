package org.example.droppydriver.dtos;

import lombok.Getter;
import lombok.Setter;
import org.example.droppydriver.models.User;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class UserResponse {

    private final UUID id;
    private String username;
    private Date createdAt;
    private String email;
    private int age;

    public UserResponse(UUID id, String username, Date createdAt, String email, int age) {
        this.id = id;
        this.username = username;
        this.createdAt = createdAt;
        this.email = email;
        this.age = age;
    }

    public static UserResponse fromModel(User user) {
        return new UserResponse(user.getId(),
                user.getUsername(),
                user.getCreatedAt(),
                user.getEmail(),
                user.getAge()
        );
    }

}
