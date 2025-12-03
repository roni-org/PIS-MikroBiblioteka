package com.mikroBiblioteka.project.tests;

import com.mikroBiblioteka.project.model.FileMeta;
import com.mikroBiblioteka.project.repository.FileMetaRepository;
import com.mikroBiblioteka.project.service.FileService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.time.LocalDateTime;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.data.mongodb.database=files-test",
                "de.flapdoodle.mongodb.embedded.version=6.0.5"
        }
)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MetaFileIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FileMetaRepository fileMetaRepository;

    @BeforeEach
    void cleanDb() {
        fileMetaRepository.deleteAll();
    }

    @Test
    void uploadFile_shouldPersistMetaInH2() throws Exception {

        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "hello world".getBytes()
        );

        mockMvc.perform(multipart("/api/files/upload")
                        .file(multipartFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("test.txt"));

        long count = fileMetaRepository.count();
        assert count == 1;
    }

    @Test
    void getAllMetaFiles_shouldReturnDataFromH2() throws Exception {
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
                .andExpect(jsonPath("$[0].name").value("abc.txt"));
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
        mockMvc.perform(get("/api/files/{id}", 999L))
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
