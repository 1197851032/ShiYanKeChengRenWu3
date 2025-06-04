package com.example.controller;

import com.example.service.DirectoryParseService;
import com.example.service.FileImportService;
import com.example.service.StatisticsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/report")
public class ReportController {
    private final FileImportService fileImportService;
    private final DirectoryParseService directoryParseService;
    private final StatisticsService statisticsService;

    public ReportController(FileImportService fileImportService,
                            DirectoryParseService directoryParseService,
                            StatisticsService statisticsService) {
        this.fileImportService = fileImportService;
        this.directoryParseService = directoryParseService;
        this.statisticsService = statisticsService;
    }

    // 接口1：学生文件导入
    @PostMapping("/import-students")
    public String importStudents(@RequestParam("file") MultipartFile file) throws Exception {
        fileImportService.importStudents(file);
        return "学生信息导入成功";
    }

    // 接口2：处理实验报告目录
    @PostMapping("/process-directory")
    public String processDirectory(@RequestParam("path") String directoryPath) {
        directoryParseService.processDirectory(directoryPath);
        return "目录处理完成，已生成提交记录";
    }

    // 接口3：学生维度缺交统计
    @GetMapping("/student-statistics")
    public List<Map<String, Object>> getStudentStatistics() {
        return statisticsService.getStudentDimensionStatistics();
    }

    // 接口4：实验维度缺交统计

    @GetMapping("/experiment-statistics")
    public List<Map<String, Object>> getExperimentStatistics() {
        return statisticsService.getExperimentDimensionStatistics();
    }
}
    