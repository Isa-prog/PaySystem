package kg.devcats.internlabs.admin.dto.response;


import kg.devcats.internlabs.core.entity.Role;

import java.util.Set;

public record UserDTO(
        Long id,
        String email,
        String username,
        String password,
        Set<Role> role
) {
}
