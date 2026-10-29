package kg.emadrasa.backend.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record LessonResponse(
        Long id,
        Long groupId,
        String groupName,
        Long subjectId,
        String subjectName,
        Long ustazId,
        String ustazName,
        LocalDate lessonDate,
        LocalTime startTime,
        LocalTime endTime,
        String topic
) {}