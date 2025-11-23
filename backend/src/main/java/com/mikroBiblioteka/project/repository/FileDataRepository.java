package com.mikroBiblioteka.project.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mikroBiblioteka.project.model.FileData;

import java.util.Optional;


public interface FileDataRepository extends MongoRepository<FileData, String> {

    void deleteByGridFsId(String gridFsId);

}
