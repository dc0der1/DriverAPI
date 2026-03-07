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
public class FolderModel {

    @Id
    private final UUID id = UUID.randomUUID();

    private String name;
    private Date createdAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel userModel;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY, mappedBy = "folderModel")
    private List<FileModel> files = new ArrayList<>();

    public FolderModel(String name, UserModel userModel) {
        this.name = name;
        this.userModel = userModel;
        this.createdAt = new Date();
    }
}
