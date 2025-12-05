package com.mikroBiblioteka.project.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "meta_files")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileMeta {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "meta_files_seq")
    @SequenceGenerator(name = "meta_files_seq", sequenceName = "meta_files_seq", allocationSize = 50)
    private Long id;

    @Column(name = "file_name", nullable = false, length = 100)
    private String name;

    @Column(name = "file_size", nullable = false)
    private Integer size;

    @Builder.Default
    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt = LocalDateTime.now();

    @Column(name = "data_id")
    private String dataId;

    @Column(name = "content_type")
    private String contentType;

    @Builder.Default
    @Column(name = "download_count", nullable = false)
    private Integer downloadCount = 0;

}
