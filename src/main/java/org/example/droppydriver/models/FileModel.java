package org.example.droppydriver.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.BinaryJdbcType;

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

    @JdbcType(BinaryJdbcType.class)
    @Column(name = "content", columnDefinition = "BYTEA")
    private byte[] data;

    @ManyToOne
    private FolderModel folderModel;

    @ManyToOne
    private UserModel owner;

}
