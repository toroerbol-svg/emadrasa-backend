package kg.emadrasa.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import kg.emadrasa.backend.enums.AttendanceStatus;

public record StudentAttendanceItem(
        @NotNull(message = "ID студента обязателен")
        @Schema(example = "2")
        Long studentId,

        @NotNull(message = "Статус обязателен (PRESENT, ABSENT, LATE, EXCUSED)")
        @Schema(example = "ABSENT")
        AttendanceStatus status,

        @Schema(example = "Уехал домой по семейным обстоятельствам")
        String comment
) {}