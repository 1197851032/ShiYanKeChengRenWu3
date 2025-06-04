package com.example.repository;

import com.example.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, String> {
    // 查询所有学生ID
    @Query("SELECT s.studentId FROM Student s")
    List<String> findAllStudentIds();
}