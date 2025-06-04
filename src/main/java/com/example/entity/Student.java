package com.example.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "students")
public class Student {
    @Id
    private String studentId;  // 学号（主键）
    private String name;       // 姓名
    private String grade;      // 年级
    private String major;      // 专业
}
    