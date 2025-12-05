package com.mikroBiblioteka.project.tests;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.mikroBiblioteka.project.controller.FileController;
import com.mikroBiblioteka.project.model.FileMeta;
import com.mikroBiblioteka.project.repository.FileMetaRepository;
import com.mikroBiblioteka.project.service.FileService;
import org.springframework.web.multipart.MultipartFile;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@WebMvcTest(controllers = {FileController.class},
    excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration.class,
        org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration.class
    })
@AutoConfigureMockMvc
class MetaFileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileMetaRepository metaFileRepository;

    @MockBean
    private FileService fileService;

    private FileMeta sampleMetaFile;

    @BeforeEach
    void setup() {
        sampleMetaFile = FileMeta.builder()
                .id(1L)
                .name("example.pdf")
                .size(2048)
                .dataId("1")
                .build();
    }

    @Test
    void shouldGetAllMetaFiles() throws Exception {
        when(fileService.getAllFileMeta()).thenReturn(Collections.singletonList(sampleMetaFile));

        mockMvc.perform(get("/api/files"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("example.pdf"));
    }

    @Test
    void shouldGetMetaFileById() throws Exception {
        when(fileService.getFileMetaById(1L)).thenReturn(Optional.of(sampleMetaFile));

        mockMvc.perform(get("/api/files/{id}", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("example.pdf"));
    }

    @Test
    void shouldCreateMetaFile() throws Exception {
        when(fileService.store(any())).thenReturn(sampleMetaFile);

        mockMvc.perform(multipart("/api/files/upload")
            .file("file", "test content".getBytes())
            .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("example.pdf"));
    }


    @Test
    void shouldDeleteMetaFile() throws Exception {
        when(fileService.getFileMetaById(sampleMetaFile.getId())).thenReturn(Optional.of(sampleMetaFile));
        doNothing().when(fileService).delete(sampleMetaFile.getId());

        mockMvc.perform(delete("/api/files/{id}", sampleMetaFile.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnNotFoundForMissingMetaFile() throws Exception {
        when(fileService.getFileMetaById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/files/{id}", 99))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenDownloadingMetaMissing() throws Exception {
        when(fileService.getFileMetaById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/files/download/{id}", 99))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenGridFsResourceMissing() throws Exception {
        when(fileService.getFileMetaById(1L)).thenReturn(Optional.of(sampleMetaFile));
        when(fileService.getFileResource(sampleMetaFile.getDataId())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/files/download/{id}", 1))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnInternalServerErrorWhenDownloadFails() throws Exception {
        when(fileService.getFileMetaById(1L))
                .thenReturn(Optional.of(sampleMetaFile));
        GridFsResource mockResource = mock(GridFsResource.class);

        when(fileService.getFileResource(sampleMetaFile.getDataId()))
                .thenReturn(Optional.of(mockResource));

        when(mockResource.getInputStream())
                .thenThrow(new IOException("Read failed"));

        mockMvc.perform(get("/api/files/download/{id}", 1))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldReturnInternalServerErrorWhenIOExceptionOccursWhileFileUpload() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        FileService fileService = mock(FileService.class);
        when(fileService.store(file)).thenThrow(new IOException("test exception"));

        FileController controller = new FileController(fileService);

        ResponseEntity<FileMeta> response = controller.uploadFile(file);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNull();
    }
}
