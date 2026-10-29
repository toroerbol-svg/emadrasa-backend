package kg.emadrasa.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kg.emadrasa.backend.dto.*;
import kg.emadrasa.backend.service.JournalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/journal")
@RequiredArgsConstructor
@Tag(name = "2. Электронный журнал (Посещаемость, Оценки, Хифз)", description = "Отметка посещаемости за 1 клик, оценки по Корану и статистика шакирда")
public class JournalController {

    private final JournalService journalService;

    @PostMapping("/lessons/{lessonId}/attendance")
    @Operation(
            summary = "Пакетная отметка посещаемости на уроке",
            description = "Передай список только отсутствующих/опоздавших студентов. Всем остальным студентам группы автоматически выставится PRESENT!"
    )
    public List<AttendanceResponse> markBatchAttendance(
            @PathVariable Long lessonId,
            @Valid @RequestBody(required = false) List<StudentAttendanceItem> items) {
        return journalService.markBatchAttendance(lessonId, items);
    }

    @GetMapping("/lessons/{lessonId}/attendance")
    @Operation(summary = "Посмотреть журнал посещаемости конкретного урока")
    public List<AttendanceResponse> getLessonAttendance(@PathVariable Long lessonId) {
        return journalService.getLessonAttendance(lessonId);
    }

    @PostMapping("/grades")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Выставить оценку (обычную или за сдачу Хифза: Сабак / Манзил)")
    public GradeResponse addGrade(@Valid @RequestBody GradeRequest request) {
        return journalService.addGrade(request);
    }

    @GetMapping("/students/{studentId}/grades")
    @Operation(summary = "Получить все оценки и историю сдачи Хифза студента")
    public List<GradeResponse> getStudentGrades(@PathVariable Long studentId) {
        return journalService.getStudentGrades(studentId);
    }

    @GetMapping("/students/{studentId}/stats")
    @Operation(summary = "Получить полную аналитику по студенту (% посещаемости, средний балл, предупреждение о пропусках)")
    public StudentStatsResponse getStudentStatistics(@PathVariable Long studentId) {
        return journalService.getStudentStatistics(studentId);
    }
}