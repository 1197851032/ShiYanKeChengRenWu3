package com.example.util;

import com.example.entity.Student;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FileParseUtil {
    // 解析学生文件（Excel或CSV）
    public static List<Student> parseStudentFile(MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();
        if (fileName == null) throw new IllegalArgumentException("文件名不能为空");

        if (fileName.endsWith(".xlsx")) {
            return parseExcel(file.getInputStream());
        } else if (fileName.endsWith(".csv")) {
            return parseCsv(file.getInputStream());
        } else {
            throw new IllegalArgumentException("仅支持.xlsx和.csv文件");
        }
    }

    // 解析Excel文件
    private static List<Student> parseExcel(InputStream inputStream) throws IOException {
        List<Student> students = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {  // 跳过表头（第0行）
                Row row = sheet.getRow(i);
                Student student = new Student();
                student.setStudentId(row.getCell(0).getStringCellValue().trim());
                student.setName(row.getCell(1).getStringCellValue().trim());
                student.setGrade(row.getCell(2).getStringCellValue().trim());
                student.setMajor(row.getCell(3).getStringCellValue().trim());
                students.add(student);
            }
        }
        return students;
    }

    // 解析CSV文件
    private static List<Student> parseCsv(InputStream inputStream) throws IOException {
        List<Student> students = new ArrayList<>();
        try (CSVParser parser = CSVParser.parse(inputStream, java.nio.charset.StandardCharsets.UTF_8,
                CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
            for (CSVRecord record : parser) {
                Student student = new Student();
                student.setStudentId(record.get(0).trim());
                student.setName(record.get(1).trim());
                student.setGrade(record.get(2).trim());
                student.setMajor(record.get(3).trim());
                students.add(student);
            }
        }
        return students;
    }
}
    