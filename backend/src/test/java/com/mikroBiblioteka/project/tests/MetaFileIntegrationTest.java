package com.mikroBiblioteka.project.tests;

import com.mikroBiblioteka.project.model.FileMeta;
import com.mikroBiblioteka.project.repository.FileMetaRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.io.InputStream;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.mongodb.embedded.version=6.0.5",
        "de.flapdoodle.mongodb.embedded.version=6.0.5",
        "spring.data.mongodb.database=files-test",
        "spring.data.mongodb.port=0"
})
class MetaFileIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FileMetaRepository fileMetaRepository;

    @MockBean
    private GridFsTemplate gridFsTemplate;

    @BeforeEach
    void cleanDb() {
        fileMetaRepository.deleteAll();
    }

    @Test
    void uploadFile_shouldPersistMetaInDb() throws Exception {
        when(gridFsTemplate.store(
                any(InputStream.class),
                any(String.class),
                any(String.class)
        )).thenReturn(new ObjectId("651111111111111111111111"));

        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "hello world".getBytes()
        );

        mockMvc.perform(
                        multipart("/api/files/upload")
                                .file(multipartFile)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("test.txt"));
        assertThat(fileMetaRepository.findByName("test.txt")).isPresent();
    }


    @Test
    void getAllMetaFiles_shouldReturnDataFromDb() throws Exception {
        FileMeta meta = fileMetaRepository.save(
                FileMeta.builder()
                        .name("abc.txt")
                        .size(3)
                        .contentType("text/plain")
                        .uploadedAt(LocalDateTime.now())
                        .dataId("grid-id")
                        .downloadCount(0)
                        .build()
        );

        mockMvc.perform(get("/api/files"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == %s)].name", meta.getId())
                        .value("abc.txt"));
    }

    @Test
    void getMetaFileById_shouldReturnMetaWhenExists() throws Exception {
        FileMeta saved = fileMetaRepository.save(
                FileMeta.builder()
                        .name("doc.txt")
                        .size(5)
                        .contentType("text/plain")
                        .uploadedAt(LocalDateTime.now())
                        .dataId("grid-id")
                        .downloadCount(0)
                        .build()
        );

        mockMvc.perform(get("/api/files/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.name").value("doc.txt"));
    }

    @Test
    void getMetaFileById_shouldReturn404WhenNotExists() throws Exception {
        mockMvc.perform(get("/api/files/{id}", 999999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteMetaFile_shouldRemoveEntityAndReturn204() throws Exception {
        String gridId = new ObjectId().toHexString();

        FileMeta saved = fileMetaRepository.save(
                FileMeta.builder()
                        .name("to-delete.txt")
                        .size(10)
                        .contentType("text/plain")
                        .uploadedAt(LocalDateTime.now())
                        .dataId(gridId)
                        .downloadCount(0)
                        .build()
        );

        mockMvc.perform(delete("/api/files/{id}", saved.getId()))
                .andExpect(status().isNoContent());

        assertThat(fileMetaRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void deleteMetaFile_shouldReturn404WhenNotExists() throws Exception {
        mockMvc.perform(delete("/api/files/{id}", 123L))
                .andExpect(status().isNotFound());
    }
}
