package com.mikroBiblioteka.project.service;

import com.mikroBiblioteka.project.model.FileMeta;
import com.mikroBiblioteka.project.model.FileData;
import com.mikroBiblioteka.project.repository.FileDataRepository;
import com.mikroBiblioteka.project.repository.FileMetaRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.mongodb.client.gridfs.model.GridFSFile;


import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class FileService {
    private final FileDataRepository dataRepo;
    private final FileMetaRepository metaRepo;
    private final GridFsTemplate gridFsTemplate;


    public FileMeta store(MultipartFile file) throws IOException {
        try (InputStream in = file.getInputStream()) {
            ObjectId gridFsId = gridFsTemplate.store(in, file.getOriginalFilename(), file.getContentType());

            FileData meta = new FileData();
            meta.setFilename(file.getOriginalFilename());
            meta.setFilesize(file.getSize());
            meta.setContentType(file.getContentType());
            meta.setGridFsId(gridFsId.toHexString());

            dataRepo.save(meta);

            FileMeta metaFile = FileMeta.builder()
                .name(file.getOriginalFilename())
                .size((int) file.getSize())
                .uploadedAt(LocalDateTime.now())
                .contentType(file.getContentType())
                .dataId(gridFsId.toHexString())
                .build();

            return metaRepo.save(metaFile);
        }
    }




    public Optional<GridFsResource> getFileResource(String gridFsId) {
        GridFSFile gfile = gridFsTemplate.findOne(
                org.springframework.data.mongodb.core.query.Query.query(
                        org.springframework.data.mongodb.core.query.Criteria.where("_id").is(new ObjectId(gridFsId))
                )
        );
        if (gfile == null) {
            return Optional.empty();
        }

        GridFsResource resource = gridFsTemplate.getResource(gfile);
        return Optional.of(resource);
    }


    public List<FileMeta> getAllFileMeta() {
        return metaRepo.findAll();
    }

    public Optional<FileMeta> getFileMetaById(Long id) {
        return metaRepo.findById(id);
    }



    public void delete(Long id) {
        Optional<FileMeta> metaOpt = metaRepo.findById(id);

        metaOpt.ifPresent(meta -> {
            String gridFsId = meta.getDataId();
            gridFsTemplate.delete(org.springframework.data.mongodb.core.query.Query.query(
                    org.springframework.data.mongodb.core.query.Criteria.where("_id").is(new ObjectId(gridFsId))
            ));
            dataRepo.deleteByGridFsId(gridFsId);
            metaRepo.delete(meta);
        });

    }
}
