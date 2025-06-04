package com.example.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
@Table(name = "submission")
@IdClass(Submission.SubmissionPK.class)
public class Submission {
    @Id
    private String experimentName;  // 实验名称（如"实验1"）
    @Id
    private String studentId;       // 学号

    // 联合主键辅助类
    @Data
    public static class SubmissionPK {
        private String experimentName;
        private String studentId;
    }
}
    