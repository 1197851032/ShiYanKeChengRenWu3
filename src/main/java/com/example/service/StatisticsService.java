package com.example.service;

import com.example.entity.Missubmission;
import com.example.entity.Student;
import com.example.repository.MissubmissionRepository;
import com.example.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticsService {
    private final MissubmissionRepository missubmissionRepository;
    private final StudentRepository studentRepository;

    public StatisticsService(MissubmissionRepository missubmissionRepository, StudentRepository studentRepository) {
        this.missubmissionRepository = missubmissionRepository;
        this.studentRepository = studentRepository;
    }

    // 接口3：学生维度缺交统计
    public List<Map<String, Object>> getStudentDimensionStatistics() {
        // 获取所有未提交记录
        List<Missubmission> missList = missubmissionRepository.findAll();

        // 按学号分组统计
        Map<String, List<Missubmission>> studentGroup = missList.stream()
                .collect(Collectors.groupingBy(Missubmission::getStudentId));

        // 关联学生姓名
        Map<String, String> studentNameMap = studentRepository.findAll().stream()
                .collect(Collectors.toMap(Student::getStudentId, Student::getName));

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, List<Missubmission>> entry : studentGroup.entrySet()) {
            String studentId = entry.getKey();
            List<String> expList = entry.getValue().stream()
                    .map(Missubmission::getExperimentName)
                    .sorted()
                    .collect(Collectors.toList());

            Map<String, Object> map = new HashMap<>();
            map.put("学号", studentId);
            map.put("姓名", studentNameMap.getOrDefault(studentId, "未知"));
            map.put("缺交次数", expList.size());
            map.put("缺交实验列表", String.join("：", expList));
            result.add(map);
        }
        return result;
    }

    // 接口4：实验维度缺交统计
    public List<Map<String, Object>> getExperimentDimensionStatistics() {
        // 获取所有未提交记录
        List<Missubmission> missList = missubmissionRepository.findAll();

        // 按实验名称分组统计
        Map<String, List<Missubmission>> expGroup = missList.stream()
                .collect(Collectors.groupingBy(Missubmission::getExperimentName));

        // 关联学生姓名
        Map<String, String> studentNameMap = studentRepository.findAll().stream()
                .collect(Collectors.toMap(Student::getStudentId, Student::getName));

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, List<Missubmission>> entry : expGroup.entrySet()) {
            String expName = entry.getKey();
            List<String> studentNames = entry.getValue().stream()
                    .map(m -> studentNameMap.getOrDefault(m.getStudentId(), "未知"))
                    .sorted()
                    .collect(Collectors.toList());

            Map<String, Object> map = new HashMap<>();
            map.put("实验名", expName);
            map.put("缺交人数", studentNames.size());
            map.put("缺交列表", String.join("：", studentNames));
            result.add(map);
        }
        return result;
    }
}
    