package com.mikroBiblioteka.project.tests;

import com.mikroBiblioteka.project.Application;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;

@WebMvcTest(Application.class)
class ApplicationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testHelloEndpoint() throws Exception {
        mockMvc.perform(get("/api/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello from Spring Boot + PostgreSQL!"));
    }

    @Test
    void testMainMethod() {
        try (MockedStatic<SpringApplication> mocked =
                     Mockito.mockStatic(org.springframework.boot.SpringApplication.class)) {

            mocked.when(() -> org.springframework.boot.SpringApplication.run(any(Class.class), any(String[].class)))
                    .thenReturn(null);

            Application.main(new String[]{});

            mocked.verify(() ->
                    org.springframework.boot.SpringApplication.run(Application.class, new String[]{}));
        }
    }
}
