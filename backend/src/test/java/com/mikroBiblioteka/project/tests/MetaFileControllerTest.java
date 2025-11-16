package com.mikroBiblioteka.project.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikroBiblioteka.project.model.FileMeta;
import com.mikroBiblioteka.project.repository.FileMetaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class MetaFileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FileMetaRepository metaFileRepository;

    private FileMeta sampleMetaFile;

    @BeforeEach
    void setup() {
        metaFileRepository.deleteAll();
        sampleMetaFile = FileMeta.builder()
                .name("example.pdf")
                .size(2048)
                .dataId("1")
                .build();
        sampleMetaFile = metaFileRepository.save(sampleMetaFile);
    }

    @Test
    void shouldGetAllMetaFiles() throws Exception {
        mockMvc.perform(get("/api/meta-files"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("example.pdf"));
    }

    @Test
    void shouldGetMetaFileById() throws Exception {
        mockMvc.perform(get("/api/meta-files/{id}", sampleMetaFile.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("example.pdf"));
    }

    @Test
    void shouldCreateMetaFile() throws Exception {
        FileMeta newFile = FileMeta.builder()
                .name("newFile.txt")
                .size(1234)
                .dataId("2")
                .build();

        mockMvc.perform(post("/api/meta-files")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newFile)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("newFile.txt"));
    }


    @Test
    void shouldDeleteMetaFile() throws Exception {
        mockMvc.perform(delete("/api/meta-files/{id}", sampleMetaFile.getId()))
                .andExpect(status().isNoContent());
    }
}
