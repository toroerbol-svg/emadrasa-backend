package kg.emadrasa.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GroupRequest(
        @NotBlank(message = "Название группы обязательно")
        @Schema(example = "2 курс — Группа Б")
        String name,

        @NotNull(message = "Курс обучения обязателен")
        @Min(1) @Max(10)
        @Schema(example = "2")
        Integer courseYear,

        @Schema(example = "Углублённое изучение Фикха и Хифза")
        String description
) {}