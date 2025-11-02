package com.mikroBiblioteka.project.controller;

import com.mikroBiblioteka.project.model.MetaFile;
import com.mikroBiblioteka.project.repository.MetaFileRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meta-files")
public class MetaFileController {

    private final MetaFileRepository metaFileRepository;

    public MetaFileController(MetaFileRepository metaFileRepository) {
        this.metaFileRepository = metaFileRepository;
    }

    @GetMapping
    public List<MetaFile> getAllMetaFiles() {
        return metaFileRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MetaFile> getMetaFileById(@PathVariable Integer id) {
        return metaFileRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public MetaFile createMetaFile(@RequestBody MetaFile metaFile) {
        return metaFileRepository.save(metaFile);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteMetaFile(@PathVariable Integer id) {
        return metaFileRepository.findById(id)
                .map(metaFile -> {
                    metaFileRepository.delete(metaFile);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
