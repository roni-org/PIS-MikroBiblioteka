package com.mikroBiblioteka.project.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikroBiblioteka.project.model.MetaFile;
import com.mikroBiblioteka.project.repository.MetaFileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MetaFileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MetaFileRepository metaFileRepository;

    private MetaFile sampleMetaFile;

    @BeforeEach
    void setup() {
        metaFileRepository.deleteAll();
        sampleMetaFile = MetaFile.builder()
                .name("example.pdf")
                .size(2048)
                .dataId(1)
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
        MetaFile newFile = MetaFile.builder()
                .name("newFile.txt")
                .size(1234)
                .dataId(2)
                .build();

        mockMvc.perform(post("/api/meta-files")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newFile)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("newFile.txt"));
    }

    @Test
    void shouldUpdateMetaFile() throws Exception {
        MetaFile updated = MetaFile.builder()
                .name("updated.pdf")
                .size(999)
                .dataId(5)
                .build();

        mockMvc.perform(put("/api/meta-files/{id}", sampleMetaFile.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("updated.pdf"));
    }

    @Test
    void shouldDeleteMetaFile() throws Exception {
        mockMvc.perform(delete("/api/meta-files/{id}", sampleMetaFile.getId()))
                .andExpect(status().isNoContent());
    }
}
