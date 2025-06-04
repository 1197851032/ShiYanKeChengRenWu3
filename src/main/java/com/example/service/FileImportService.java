package com.example.service;

import com.example.entity.Student;
import com.example.repository.StudentRepository;
import com.example.util.FileParseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FileImportService {
    private final StudentRepository studentRepository;

    public void importStudents(MultipartFile file) throws Exception {
        List<Student> students = FileParseUtil.parseStudentFile(file);
        studentRepository.saveAll(students);  // 批量保存学生信息
    }
}
    