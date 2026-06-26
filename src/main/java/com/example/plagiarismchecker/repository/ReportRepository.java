package com.example.plagiarismchecker.repository;

import com.example.plagiarismchecker.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findAllByOrderByCreatedAtDesc();

    List<Report> findByUserIdOrderByCreatedAtDesc(Long userId);
}