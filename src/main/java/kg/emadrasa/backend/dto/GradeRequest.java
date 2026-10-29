package kg.emadrasa.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import kg.emadrasa.backend.enums.GradeType;

public record GradeRequest(
        @NotNull(message = "ID урока обязателен")
        @Schema(example = "1")
        Long lessonId,

        @NotNull(message = "ID студента обязателен")
        @Schema(example = "2")
        Long studentId,

        @NotNull(message = "Оценка обязательна (от 1 до 5)")
        @Min(value = 1, message = "Минимальная оценка — 1")
        @Max(value = 5, message = "Максимальная оценка — 5")
        @Schema(example = "5")
        Integer score,

        @NotNull(message = "Тип оценки обязателен")
        @Schema(example = "HIFZ_SABAK")
        GradeType gradeType,

        @Schema(example = "Сура Аль-Мульк, аяты 1-15")
        String surahAndAyahs,

        @Schema(example = "Отличное чтение, соблюдены все правила махраджа и гуны")
        String comment
) {}