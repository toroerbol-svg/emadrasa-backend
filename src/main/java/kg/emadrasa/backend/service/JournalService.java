package kg.emadrasa.backend.service;

import kg.emadrasa.backend.dto.*;
import kg.emadrasa.backend.entity.*;
import kg.emadrasa.backend.enums.AttendanceStatus;
import kg.emadrasa.backend.enums.GradeType;
import kg.emadrasa.backend.enums.Role;
import kg.emadrasa.backend.exception.ResourceNotFoundException;
import kg.emadrasa.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JournalService {

    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;
    private final AttendanceRepository attendanceRepository;
    private final GradeRepository gradeRepository;

    /**
     * Умная пакетная отметка посещаемости:
     * Берет всех студентов группы урока. Если устаз не указал студента в списке исключений,
     * автоматически ставит ему статус PRESENT (Присутствовал).
     */
    @Transactional
    public List<AttendanceResponse> markBatchAttendance(Long lessonId, List<StudentAttendanceItem> items) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Урок не найден с ID: " + lessonId));

        List<User> groupStudents = userRepository.findAllByGroupIdAndRoleOrderByFullNameAsc(
                lesson.getGroup().getId(), Role.STUDENT
        );

        if (groupStudents.isEmpty()) {
            throw new IllegalArgumentException("В группе " + lesson.getGroup().getName() + " пока нет студентов");
        }

        Map<Long, StudentAttendanceItem> customMap = (items == null ? List.<StudentAttendanceItem>of() : items)
                .stream()
                .collect(Collectors.toMap(
                        StudentAttendanceItem::studentId,
                        item -> item,
                        (existing, replacement) -> replacement
                ));

        List<Attendance> savedList = new ArrayList<>();

        for (User student : groupStudents) {
            StudentAttendanceItem customItem = customMap.get(student.getId());
            AttendanceStatus status = customItem != null ? customItem.status() : AttendanceStatus.PRESENT;
            String comment = customItem != null ? customItem.comment() : null;

            Attendance attendance = attendanceRepository.findByLessonIdAndStudentId(lessonId, student.getId())
                    .orElseGet(() -> Attendance.builder()
                            .lesson(lesson)
                            .student(student)
                            .build());

            attendance.setStatus(status);
            attendance.setComment(comment);
            attendance.setMarkedAt(LocalDateTime.now());

            savedList.add(attendanceRepository.save(attendance));
        }

        return savedList.stream()
                .map(this::mapToAttendanceResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AttendanceResponse> getLessonAttendance(Long lessonId) {
        return attendanceRepository.findAllByLessonId(lessonId)
                .stream()
                .map(this::mapToAttendanceResponse)
                .toList();
    }

    // --- ОЦЕНКИ И ХИФЗ ---
    @Transactional
    public GradeResponse addGrade(GradeRequest request) {
        Lesson lesson = lessonRepository.findById(request.lessonId())
                .orElseThrow(() -> new ResourceNotFoundException("Урок не найден с ID: " + request.lessonId()));

        User student = userRepository.findById(request.studentId())
                .orElseThrow(() -> new ResourceNotFoundException("Студент не найден с ID: " + request.studentId()));

        if (student.getRole() != Role.STUDENT) {
            throw new IllegalArgumentException("Оценку можно выставить только пользователю с ролью STUDENT");
        }

        Grade grade = Grade.builder()
                .lesson(lesson)
                .student(student)
                .score(request.score())
                .gradeType(request.gradeType())
                .surahAndAyahs(request.surahAndAyahs())
                .comment(request.comment())
                .build();

        return mapToGradeResponse(gradeRepository.save(grade));
    }

    @Transactional(readOnly = true)
    public List<GradeResponse> getStudentGrades(Long studentId) {
        return gradeRepository.findAllByStudentIdOrderByCreatedAtDesc(studentId)
                .stream()
                .map(this::mapToGradeResponse)
                .toList();
    }

    // --- АНАЛИТИКА И КОНТРОЛЬ ПРОПУСКОВ СТУДЕНТА ---
    @Transactional(readOnly = true)
    public StudentStatsResponse getStudentStatistics(Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Студент не найден с ID: " + studentId));

        List<Attendance> attendances = attendanceRepository.findAllByStudentIdOrderByMarkedAtDesc(studentId);
        List<Grade> grades = gradeRepository.findAllByStudentIdOrderByCreatedAtDesc(studentId);

        long totalLessons = attendances.size();
        long presentCount = attendances.stream().filter(a -> a.getStatus() == AttendanceStatus.PRESENT).count();
        long lateCount = attendances.stream().filter(a -> a.getStatus() == AttendanceStatus.LATE).count();
        long excusedCount = attendances.stream().filter(a -> a.getStatus() == AttendanceStatus.EXCUSED).count();
        long absentCount = attendances.stream().filter(a -> a.getStatus() == AttendanceStatus.ABSENT).count();

        double attendancePercentage = totalLessons == 0 ? 100.0 :
                Math.round(((double) (presentCount + lateCount) / totalLessons) * 1000.0) / 10.0;

        double averageGrade = grades.isEmpty() ? 0.0 :
                Math.round(grades.stream().mapToInt(Grade::getScore).average().orElse(0.0) * 100.0) / 100.0;

        long hifzSabakCount = grades.stream()
                .filter(g -> g.getGradeType() == GradeType.HIFZ_SABAK)
                .count();

        boolean warningAlert = absentCount >= 3;
        String statusMessage = warningAlert
                ? "ВНИМАНИЕ: Превышен лимит пропусков без уважительной причины (" + absentCount + ")! Требуется связаться с родителями."
                : "Посещаемость в норме.";

        return new StudentStatsResponse(
                student.getId(),
                student.getFullName(),
                student.getGroup() != null ? student.getGroup().getName() : "Без группы",
                totalLessons,
                presentCount,
                lateCount,
                excusedCount,
                absentCount,
                attendancePercentage,
                averageGrade,
                hifzSabakCount,
                warningAlert,
                statusMessage
        );
    }

    private AttendanceResponse mapToAttendanceResponse(Attendance a) {
        return new AttendanceResponse(
                a.getId(),
                a.getLesson().getId(),
                a.getLesson().getSubject().getName(),
                a.getStudent().getId(),
                a.getStudent().getFullName(),
                a.getStatus(),
                a.getComment(),
                a.getMarkedAt()
        );
    }

    private GradeResponse mapToGradeResponse(Grade g) {
        return new GradeResponse(
                g.getId(),
                g.getLesson().getId(),
                g.getLesson().getSubject().getName(),
                g.getLesson().getLessonDate(),
                g.getStudent().getId(),
                g.getStudent().getFullName(),
                g.getScore(),
                g.getGradeType(),
                g.getSurahAndAyahs(),
                g.getComment(),
                g.getCreatedAt()
        );
    }
}