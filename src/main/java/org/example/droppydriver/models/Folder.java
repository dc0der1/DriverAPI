package org.example.droppydriver.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Entity(name = "folders")
@Getter
@Setter
@NoArgsConstructor
public class Folder {

    @Id
    private final UUID id = UUID.randomUUID();

    private String name;
    private Date createdAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER, mappedBy = "folder")
    private List<FileModel> files = new ArrayList<>();

    public Folder(String name, User user) {
        this.name = name;
        this.user = user;
        this.createdAt = new Date();
    }
}
