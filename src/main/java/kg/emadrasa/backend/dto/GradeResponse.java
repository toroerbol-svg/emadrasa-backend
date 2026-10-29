package kg.emadrasa.backend.dto;

import kg.emadrasa.backend.enums.GradeType;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record GradeResponse(
        Long id,
        Long lessonId,
        String subjectName,
        LocalDate lessonDate,
        Long studentId,
        String studentName,
        Integer score,
        GradeType gradeType,
        String surahAndAyahs,
        String comment,
        LocalDateTime createdAt
) {}