package kg.emadrasa.backend.dto;

import kg.emadrasa.backend.enums.AttendanceStatus;
import java.time.LocalDateTime;

public record AttendanceResponse(
        Long id,
        Long lessonId,
        String subjectName,
        Long studentId,
        String studentName,
        AttendanceStatus status,
        String comment,
        LocalDateTime markedAt
) {}