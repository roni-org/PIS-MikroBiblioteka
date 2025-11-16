package com.mikroBiblioteka.project.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mikroBiblioteka.project.model.FileData;


public interface FileDataRepository extends MongoRepository<FileData, String> {
}
