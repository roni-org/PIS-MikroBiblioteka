package com.mikroBiblioteka.project.controller;

import com.mikroBiblioteka.project.model.FileMeta;
import com.mikroBiblioteka.project.repository.FileMetaRepository;
import com.mikroBiblioteka.project.service.FileService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class FileController {

    private final FileMetaRepository metaFileRepository;
    private final FileService fileService;

    @GetMapping
    public List<FileMeta> getAllMetaFiles() {
        return metaFileRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<FileMeta> getMetaFileById(@PathVariable Integer id) {
        return metaFileRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileMeta> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "dataId", required = false) Integer dataId) {

        try {
            FileMeta saved = fileService.store(file);

            return ResponseEntity.ok(saved);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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
