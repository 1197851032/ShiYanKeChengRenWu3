package com.example.repository;

import com.example.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    // 查询所有实验名称
    @Query("SELECT DISTINCT s.experimentName FROM Submission s")
    List<String> findAllExperimentNames();

    // 查询指定实验的所有已提交学生ID
    @Query("SELECT s.studentId FROM Submission s WHERE s.experimentName = :experimentName")
    List<String> findStudentIdsByExperiment(String experimentName);
}