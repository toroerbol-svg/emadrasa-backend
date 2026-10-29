package kg.emadrasa.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.emadrasa.backend.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Tag(name = "3. Отчеты и документы", description = "Генерация и скачивание ведомостей в формате Excel")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/lessons/{lessonId}/excel")
    @Operation(summary = "Скачать ведомость посещаемости урока в формате Excel (.xlsx)")
    public ResponseEntity<Resource> downloadAttendanceExcel(@PathVariable Long lessonId) {
        ByteArrayInputStream actualStream = reportService.generateAttendanceExcel(lessonId);
        InputStreamResource fileStream = new InputStreamResource(actualStream);

        String filename = "attendance_lesson_" + lessonId + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(fileStream);
    }
}