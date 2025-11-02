package com.mikroBiblioteka.project.repository;

import com.mikroBiblioteka.project.model.MetaFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetaFileRepository extends JpaRepository<MetaFile, Integer> {
}
