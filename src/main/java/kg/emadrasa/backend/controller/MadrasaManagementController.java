package kg.emadrasa.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kg.emadrasa.backend.dto.*;
import kg.emadrasa.backend.entity.StudyGroup;
import kg.emadrasa.backend.entity.Subject;
import kg.emadrasa.backend.enums.Role;
import kg.emadrasa.backend.service.MadrasaManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "1. Управление медресе и расписанием", description = "Группы, предметы, устазы, студенты и расписание уроков")
public class MadrasaManagementController {

    private final MadrasaManagementService managementService;

    @GetMapping("/groups")
    @Operation(summary = "Получить список всех учебных групп")
    public List<StudyGroup> getAllGroups() {
        return managementService.getAllGroups();
    }

    @PostMapping("/groups")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать новую учебную группу")
    public StudyGroup createGroup(@Valid @RequestBody GroupRequest request) {
        return managementService.createGroup(request);
    }

    @GetMapping("/subjects")
    @Operation(summary = "Получить список всех предметов (Таджвид, Хифз, Фикх и др.)")
    public List<Subject> getAllSubjects() {
        return managementService.getAllSubjects();
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Зарегистрировать пользователя (Устаза, Студента или Админа)")
    public UserResponse createUser(@Valid @RequestBody UserRequest request) {
        return managementService.createUser(request);
    }

    @GetMapping("/groups/{groupId}/students")
    @Operation(summary = "Получить список всех студентов конкретной группы")
    public List<UserResponse> getStudentsByGroup(@PathVariable Long groupId) {
        return managementService.getStudentsByGroup(groupId);
    }

    @GetMapping("/users")
    @Operation(summary = "Получить список пользователей по роли (USTAZ, STUDENT, ADMIN)")
    public List<UserResponse> getUsersByRole(@RequestParam Role role) {
        return managementService.getUsersByRole(role);
    }

    @PostMapping("/lessons")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Добавить урок в расписание")
    public LessonResponse createLesson(@Valid @RequestBody LessonRequest request) {
        return managementService.createLesson(request);
    }

    @GetMapping("/groups/{groupId}/schedule")
    @Operation(summary = "Получить расписание группы на выбранную дату")
    public List<LessonResponse> getGroupSchedule(
            @PathVariable Long groupId,
            @RequestParam LocalDate date) {
        return managementService.getGroupSchedule(groupId, date);
    }
}