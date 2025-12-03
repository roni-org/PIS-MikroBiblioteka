package com.mikroBiblioteka.project.repository;

import com.mikroBiblioteka.project.model.FileMeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileMetaRepository extends JpaRepository<FileMeta, Long> {
    Optional<FileMeta> findByName(String file);
}
