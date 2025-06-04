package com.example.service;
import com.example.entity.Missubmission;
import com.example.entity.Student;
import com.example.entity.Submission;
import com.example.repository.MissubmissionRepository;
import com.example.repository.StudentRepository;
import com.example.repository.SubmissionRepository;
import com.example.util.DirectoryTraversalUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DirectoryParseService {
    private final SubmissionRepository submissionRepository;
    private final MissubmissionRepository missubmissionRepository;
    private final StudentRepository studentRepository;
    private final Logger logger = LoggerFactory.getLogger(DirectoryParseService.class);

    @Transactional
    public void processDirectory(String directoryPath) {
        submissionRepository.deleteAll();
        missubmissionRepository.deleteAll();
        logger.info("旧数据已清空");

        // 解析提交记录并保存
        List<Submission> submissions = DirectoryTraversalUtil.parseDirectory(new File(directoryPath));
        if (submissions.isEmpty()) {
            logger.warn("未解析到提交记录，缺交记录为空");
            return;
        }
        submissionRepository.saveAll(submissions);
        logger.info("已保存 {} 条提交记录", submissions.size());

        // 【关键修复】获取全体学生ID（去除"学号"前缀并标准化）
        List<Student> allStudents = studentRepository.findAll();
        if (allStudents.isEmpty()) {
            logger.error("学生表为空，终止流程");
            throw new IllegalStateException("学生表无数据");
        }
        Set<String> allStudentIds = allStudents.stream()
                .map(Student::getStudentId)
                .map(id -> id.replace("学号", "").trim().toLowerCase())  // 去除"学号"前缀+去空格+转小写
                .collect(Collectors.toSet());
        logger.info("全体学生ID（标准化后）：{}", allStudentIds);

        // 获取所有实验名称（标准化）
        Set<String> experimentNames = submissions.stream()
                .map(sub -> sub.getExperimentName().trim().toLowerCase())  // 去空格+转小写
                .collect(Collectors.toSet());
        if (experimentNames.isEmpty()) {
            logger.error("无有效实验名称，终止流程");
            return;
        }
        logger.info("待处理实验：{}", experimentNames);

        // 按实验计算缺交学生（【关键修复】已提交学生ID同步去前缀）
        for (String expName : experimentNames) {
            // 获取当前实验已提交的学生ID（去除"学号"前缀+标准化）
            Set<String> submittedStudentIds = submissions.stream()
                    .filter(sub -> expName.equals(sub.getExperimentName().trim().toLowerCase()))  // 匹配标准化后的实验名称
                    .map(sub -> sub.getStudentId().replace("学号", "").trim().toLowerCase())  // 去除"学号"前缀+去空格+转小写
                    .collect(Collectors.toSet());
            logger.info("实验[{}]已提交学生ID（标准化后）：{}", expName, submittedStudentIds);

            // 计算缺交学生（全体学生ID - 已提交学生ID）
            Set<String> unsubmittedStudentIds = allStudentIds.stream()
                    .filter(stdId -> !submittedStudentIds.contains(stdId))
                    .collect(Collectors.toSet());
            logger.info("实验[{}]缺交学生数量：{}", expName, unsubmittedStudentIds.size());

            // 保存缺交记录
            if (!unsubmittedStudentIds.isEmpty()) {
                List<Missubmission> missubmissions = unsubmittedStudentIds.stream()
                        .map(stdId -> {
                            Missubmission miss = new Missubmission();
                            miss.setExperimentName(expName);
                            miss.setStudentId(stdId);  // 保存标准化后的学生ID（无"学号"前缀）
                            return miss;
                        })
                        .collect(Collectors.toList());
                missubmissionRepository.saveAll(missubmissions);
                logger.info("实验[{}]保存{}条缺交记录", expName, missubmissions.size());
            } else {
                logger.info("实验[{}]无缺交学生", expName);
            }
        }
    }
}