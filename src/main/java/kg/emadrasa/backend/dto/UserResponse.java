package kg.emadrasa.backend.dto;

import kg.emadrasa.backend.enums.Role;

public record UserResponse(
        Long id,
        String fullName,
        String username,
        Role role,
        String phone,
        String parentPhone,
        Long groupId,
        String groupName,
        Boolean active
) {}