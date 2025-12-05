package com.mikroBiblioteka.project.tests;

import com.mikroBiblioteka.project.model.FileData;
import com.mikroBiblioteka.project.model.FileMeta;

import com.mikroBiblioteka.project.repository.FileDataRepository;
import com.mikroBiblioteka.project.repository.FileMetaRepository;
import com.mikroBiblioteka.project.service.FileService;
import com.mongodb.client.gridfs.model.GridFSFile;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class FileServiceTest {

    @Mock
    private FileDataRepository dataRepo;

    @Mock
    private FileMetaRepository metaRepo;

    @Mock
    private GridFsTemplate gridFsTemplate;

    @InjectMocks
    private FileService fileService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testStoreFileSuccessfully() throws IOException {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test-content".getBytes()
        );

        ObjectId mockId = new ObjectId("65c1d2f6e1d32b5e9c9ad111");

        when(gridFsTemplate.store(
                any(InputStream.class),
                eq("test.jpg"),
                eq("image/jpeg")
        )).thenReturn(mockId);

        FileMeta savedMeta = FileMeta.builder()
                .name("test.jpg")
                .size((int) mockFile.getSize())
                .contentType("image/jpeg")
                .dataId(mockId.toHexString())
                .downloadCount(0)
                .uploadedAt(LocalDateTime.now())
                .build();

        when(metaRepo.save(any(FileMeta.class))).thenReturn(savedMeta);

        FileMeta result = fileService.store(mockFile);

        assertNotNull(result);
        assertEquals("test.jpg", result.getName());
        assertEquals(mockId.toHexString(), result.getDataId());
        assertEquals("image/jpeg", result.getContentType());
        assertEquals((int) mockFile.getSize(), result.getSize());

        verify(dataRepo, times(1)).save(any(FileData.class));

        verify(metaRepo, times(1)).save(any(FileMeta.class));

        verify(gridFsTemplate, times(1)).store(any(InputStream.class), eq("test.jpg"), eq("image/jpeg"));
    }

    @Test
    void testGetFileResourceFound() {
        String gridFsId = new ObjectId().toHexString();
        GridFSFile gfile = mock(GridFSFile.class);
        GridFsResource resource = mock(GridFsResource.class);

        when(gridFsTemplate.findOne(any())).thenReturn(gfile);
        when(gridFsTemplate.getResource(gfile)).thenReturn(resource);

        Optional<GridFsResource> result = fileService.getFileResource(gridFsId);

        assertTrue(result.isPresent());
        assertEquals(resource, result.get());

        verify(gridFsTemplate, times(1)).findOne(any());
        verify(gridFsTemplate, times(1)).getResource(gfile);
    }


    @Test
    void testGetFileResourceNotFound() {
        String gridFsId = new ObjectId().toHexString();
        when(gridFsTemplate.findOne(any(Query.class))).thenReturn(null);

        Optional<GridFsResource> result = fileService.getFileResource(gridFsId);

        assertFalse(result.isPresent());
        verify(gridFsTemplate, times(1)).findOne(any(Query.class));
        verify(gridFsTemplate, never()).getResource(Mockito.<GridFSFile>any());
    }

    // ------------------ getAllFileMeta ------------------
    @Test
    void testGetAllFileMeta() {
        List<FileMeta> metaList = List.of(
                FileMeta.builder().id(1L).name("file1").build(),
                FileMeta.builder().id(2L).name("file2").build()
        );

        when(metaRepo.findAll()).thenReturn(metaList);

        List<FileMeta> result = fileService.getAllFileMeta();

        assertEquals(2, result.size());
        assertEquals("file1", result.get(0).getName());
        verify(metaRepo, times(1)).findAll();
    }

    // ------------------ getFileMetaById ------------------
    @Test
    void testGetFileMetaByIdFound() {
        FileMeta meta = FileMeta.builder().id(1L).name("file1").build();
        when(metaRepo.findById(1L)).thenReturn(Optional.of(meta));

        Optional<FileMeta> result = fileService.getFileMetaById(1L);

        assertTrue(result.isPresent());
        assertEquals(meta, result.get());
        verify(metaRepo, times(1)).findById(1L);
    }

    @Test
    void testGetFileMetaByIdNotFound() {
        when(metaRepo.findById(1L)).thenReturn(Optional.empty());

        Optional<FileMeta> result = fileService.getFileMetaById(1L);

        assertFalse(result.isPresent());
        verify(metaRepo, times(1)).findById(1L);
    }

    // ------------------ delete ------------------
    @Test
    void testDeleteExistingMeta() {
        String gridFsId = new ObjectId().toHexString();
        FileMeta meta = FileMeta.builder().id(1L).dataId(gridFsId).build();
        when(metaRepo.findById(1L)).thenReturn(Optional.of(meta));

        fileService.delete(1L);

        verify(gridFsTemplate, times(1)).delete(any());
        verify(dataRepo, times(1)).deleteByGridFsId(gridFsId);
        verify(metaRepo, times(1)).delete(meta);
    }

    @Test
    void testDeleteNonExistingMeta() {
        when(metaRepo.findById(1L)).thenReturn(Optional.empty());

        fileService.delete(1L);

        verify(gridFsTemplate, never()).delete(any());
        verify(dataRepo, never()).deleteByGridFsId(anyString());
        verify(metaRepo, never()).delete(any());
    }

    // ------------------ incrementDownloadCount ------------------
    @Test
    void testIncrementDownloadCount() {
        FileMeta meta = FileMeta.builder().downloadCount(3).build();
        when(metaRepo.save(meta)).thenReturn(meta);

        fileService.incrementDownloadCount(meta);

        assertEquals(4, meta.getDownloadCount());
        verify(metaRepo, times(1)).save(meta);
    }

}
