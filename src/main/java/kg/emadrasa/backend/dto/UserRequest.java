package kg.emadrasa.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kg.emadrasa.backend.enums.Role;

public record UserRequest(
        @NotBlank(message = "ФИО обязательно")
        @Schema(example = "Асанов Бакытбек")
        String fullName,

        @NotBlank(message = "Логин обязателен")
        @Schema(example = "bakyt_student")
        String username,

        @NotBlank(message = "Пароль обязателен")
        @Schema(example = "123456")
        String password,

        @NotNull(message = "Роль обязательна (ADMIN, USTAZ, STUDENT)")
        @Schema(example = "STUDENT")
        Role role,

        @Schema(example = "+996700112233")
        String phone,

        @Schema(example = "+996555998877")
        String parentPhone,

        @Schema(example = "1", description = "ID учебной группы (для студентов)")
        Long groupId
) {}