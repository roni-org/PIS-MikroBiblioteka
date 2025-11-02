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

    @PutMapping("/{id}")
    public ResponseEntity<MetaFile> updateMetaFile(@PathVariable Integer id, @RequestBody MetaFile updatedFile) {
        return metaFileRepository.findById(id)
                .map(metaFile -> {
                    metaFile.setName(updatedFile.getName());
                    metaFile.setSize(updatedFile.getSize());
                    metaFile.setDataId(updatedFile.getDataId());
                    return ResponseEntity.ok(metaFileRepository.save(metaFile));
                })
                .orElse(ResponseEntity.notFound().build());
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
