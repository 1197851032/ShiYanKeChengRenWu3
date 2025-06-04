package com.example.util;

import com.example.entity.Submission;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DirectoryTraversalUtil {
    // 文件名正则匹配（实验x_学号xxx_姓名.doc）
    private static final Pattern FILE_NAME_PATTERN = Pattern.compile("实验(\\d+)_学号(\\d+)_(.+)\\.doc");

    // 解析目录结构，生成已提交记录
    public static List<Submission> parseDirectory(File directory) {
        List<Submission> submissions = new ArrayList<>();

        if (!directory.exists() || !directory.isDirectory()) {
            throw new IllegalArgumentException("目录路径无效");
        }

        // 遍历所有班级目录（一级目录）
        File[] classDirs = directory.listFiles(File::isDirectory);
        if (classDirs == null) return submissions;

        for (File classDir : classDirs) {
            // 遍历所有实验目录（二级目录）
            File[] expDirs = classDir.listFiles(File::isDirectory);
            if (expDirs == null) continue;

            for (File expDir : expDirs) {
                String experimentName = expDir.getName();  // 实验名称（如"实验1"）
                // 遍历实验目录下的文档（三级文件）
                File[] docFiles = expDir.listFiles(f -> f.isFile() && f.getName().endsWith(".doc"));
                if (docFiles == null) continue;

                for (File docFile : docFiles) {
                    Matcher matcher = FILE_NAME_PATTERN.matcher(docFile.getName());
                    if (matcher.matches()) {
                        Submission submission = new Submission();
                        submission.setExperimentName(experimentName);
                        submission.setStudentId("学号" + matcher.group(2));  // 学号（如"学号001"）
                        submissions.add(submission);
                    }
                }
            }
        }
        return submissions;
    }
}
    