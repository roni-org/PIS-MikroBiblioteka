package com.mikroBiblioteka.project.tests;

import com.mikroBiblioteka.project.controller.FileController;
import com.mikroBiblioteka.project.model.FileMeta;
import com.mikroBiblioteka.project.service.FileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.mock;

@WebMvcTest(controllers = FileController.class)
@ActiveProfiles("test")
class MetaFileIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileService fileService;

    @Test
    void getAllMetaFiles_shouldReturnJsonArray() throws Exception {
        FileMeta meta = FileMeta.builder()
                .id(1L)
                .name("test.txt")
                .size(11)
                .contentType("text/plain")
                .uploadedAt(LocalDateTime.now())
                .dataId("some-id")
                .downloadCount(0)
                .build();

        when(fileService.getAllFileMeta()).thenReturn(List.of(meta));

        mockMvc.perform(get("/api/files"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("test.txt"));
    }

    @Test
    void getMetaFileById_shouldReturn200WhenExists() throws Exception {
        FileMeta meta = FileMeta.builder()
                .id(1L)
                .name("test.txt")
                .size(11)
                .contentType("text/plain")
                .uploadedAt(LocalDateTime.now())
                .dataId("some-id")
                .downloadCount(0)
                .build();

        when(fileService.getFileMetaById(1L)).thenReturn(Optional.of(meta));

        mockMvc.perform(get("/api/files/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("test.txt"));
    }

    @Test
    void getMetaFileById_shouldReturn404WhenNotExists() throws Exception {
        when(fileService.getFileMetaById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/files/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void uploadFile_shouldReturnSavedMeta() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "hello world".getBytes()
        );

        FileMeta saved = FileMeta.builder()
                .id(1L)
                .name("test.txt")
                .size(11)
                .contentType("text/plain")
                .uploadedAt(LocalDateTime.now())
                .dataId("some-id")
                .downloadCount(0)
                .build();

        when(fileService.store(any())).thenReturn(saved);

        mockMvc.perform(multipart("/api/files/upload").file(multipartFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("test.txt"));

        verify(fileService).store(any());
    }

    @Test
    void deleteMetaFile_shouldReturn204WhenExists() throws Exception {
        FileMeta meta = FileMeta.builder()
                .id(1L)
                .name("test.txt")
                .size(11)
                .contentType("text/plain")
                .uploadedAt(LocalDateTime.now())
                .dataId("some-id")
                .downloadCount(0)
                .build();

        when(fileService.getFileMetaById(1L)).thenReturn(Optional.of(meta));

        mockMvc.perform(delete("/api/files/1"))
                .andExpect(status().isNoContent());

        verify(fileService).delete(1L);
    }

    @Test
    void deleteMetaFile_shouldReturn404WhenNotExists() throws Exception {
        when(fileService.getFileMetaById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/files/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void downloadFile_shouldReturnFileContentAndHeaders() throws Exception {
        FileMeta meta = FileMeta.builder()
                .id(1L)
                .name("test.txt")
                .size(5)
                .contentType("text/plain")
                .uploadedAt(LocalDateTime.now())
                .dataId("gridfs-id")
                .downloadCount(0)
                .build();

        byte[] data = "hello".getBytes();

        GridFsResource resource = mock(GridFsResource.class);
        when(resource.getInputStream()).thenReturn(new ByteArrayInputStream(data));
        when(resource.contentLength()).thenReturn((long) data.length);
        when(resource.getFilename()).thenReturn("test.txt");
        when(resource.getContentType()).thenReturn("text/plain");

        when(fileService.getFileMetaById(1L)).thenReturn(Optional.of(meta));
        when(fileService.getFileResource("gridfs-id")).thenReturn(Optional.of(resource));

        mockMvc.perform(get("/api/files/download/1"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"test.txt\""))
                .andExpect(header().string("Content-Length", String.valueOf(data.length)))
                .andExpect(content().contentType(MediaType.TEXT_PLAIN))
                .andExpect(content().bytes(data));

        verify(fileService).incrementDownloadCount(meta);
    }
}
