package org.example.droppydriver.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity(name = "files")
@Getter
@Setter
@NoArgsConstructor
public class FileModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
    private String contentType;
    private Long size;

    @Lob
    @Column(name = "content", columnDefinition = "BYTEA")
    private byte[] data;

    @ManyToOne
    private Folder folder;

    @ManyToOne
    private User owner;

}
