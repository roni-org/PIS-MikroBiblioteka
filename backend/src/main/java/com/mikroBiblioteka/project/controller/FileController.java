package com.mikroBiblioteka.project.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mikroBiblioteka.project.model.FileMeta;
import com.mikroBiblioteka.project.service.FileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = {
        "http://localhost:4200",
        "http://20.19.89.210:4200"
})
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;

    @GetMapping
    public List<FileMeta> getAllMetaFiles() {
        return fileService.getAllFileMeta();
    }

    @GetMapping("/{id}")
    public ResponseEntity<FileMeta> getMetaFileById(@PathVariable Long id) {
        return fileService.getFileMetaById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileMeta> uploadFile(
            @RequestParam("file") MultipartFile file)
            {

        try {
            FileMeta saved = fileService.store(file);

            return ResponseEntity.ok(saved);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteMetaFile(@PathVariable Long id) {
        return fileService.getFileMetaById(id)
                .map(metaFile -> {
                    fileService.delete(id);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
