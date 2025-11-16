package com.mikroBiblioteka.project.repository;

import com.mikroBiblioteka.project.model.FileMeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileMetaRepository extends JpaRepository<FileMeta, Long> {
}
