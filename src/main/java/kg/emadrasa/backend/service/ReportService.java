package kg.emadrasa.backend.service;

import kg.emadrasa.backend.entity.Attendance;
import kg.emadrasa.backend.entity.Lesson;
import kg.emadrasa.backend.exception.ResourceNotFoundException;
import kg.emadrasa.backend.repository.AttendanceRepository;
import kg.emadrasa.backend.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final LessonRepository lessonRepository;
    private final AttendanceRepository attendanceRepository;

    public ByteArrayInputStream generateAttendanceExcel(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Урок не найден с ID: " + lessonId));

        List<Attendance> attendances = attendanceRepository.findAllByLessonId(lessonId);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Посещаемость");

            // Стиль заголовка
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            headerCellStyle.setFillForegroundColor(IndexedColors.DARK_GREEN.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerCellStyle.setAlignment(HorizontalAlignment.CENTER);

            // Информация об уроке
            Row infoRow1 = sheet.createRow(0);
            infoRow1.createCell(0).setCellValue("Предмет:");
            infoRow1.createCell(1).setCellValue(lesson.getSubject().getName());

            Row infoRow2 = sheet.createRow(1);
            infoRow2.createCell(0).setCellValue("Группа:");
            infoRow2.createCell(1).setCellValue(lesson.getGroup().getName());

            Row infoRow3 = sheet.createRow(2);
            infoRow3.createCell(0).setCellValue("Дата урока:");
            infoRow3.createCell(1).setCellValue(lesson.getLessonDate().toString());

            // Шапка таблицы
            String[] columns = {"№", "ФИО Студента", "Статус", "Комментарий"};
            Row headerRow = sheet.createRow(4);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerCellStyle);
            }

            // Заполнение данных
            int rowIdx = 5;
            int index = 1;
            for (Attendance att : attendances) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(index++);
                row.createCell(1).setCellValue(att.getStudent().getFullName());
                row.createCell(2).setCellValue(att.getStatus().name());
                row.createCell(3).setCellValue(att.getComment() != null ? att.getComment() : "-");
            }

            // Автоподбор ширины колонок
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при генерации Excel отчета: " + e.getMessage());
        }
    }
}