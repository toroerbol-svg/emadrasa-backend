package kg.emadrasa.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public record LessonRequest(
        @NotNull(message = "ID группы обязателен")
        @Schema(example = "1")
        Long groupId,

        @NotNull(message = "ID предмета обязателен")
        @Schema(example = "1")
        Long subjectId,

        @NotNull(message = "ID устаза обязателен")
        @Schema(example = "1")
        Long ustazId,

        @NotNull(message = "Дата урока обязательна")
        @Schema(example = "2026-10-29")
        LocalDate lessonDate,

        @NotNull(message = "Время начала обязательно")
        @Schema(type = "string", example = "09:00:00")
        LocalTime startTime,

        @NotNull(message = "Время окончания обязательно")
        @Schema(type = "string", example = "10:30:00")
        LocalTime endTime,

        @Schema(example = "Правила чтения Нун сакина и Танвин")
        String topic
) {}