package com.example.securecustomerapi.dto;

import com.example.securecustomerapi.entity.enums.Role;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRoleDTO {

    @NotNull(message = "Role is required")
    private Role role;
}
