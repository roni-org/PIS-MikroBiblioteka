package com.mikroBiblioteka.project.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "file_data")
public class FileData {
    @Id
    private String id;
    private String filename;
    private long filesize;
    private String contentType;
    private String gridFsId; // reference to file stored in GridFS
}
