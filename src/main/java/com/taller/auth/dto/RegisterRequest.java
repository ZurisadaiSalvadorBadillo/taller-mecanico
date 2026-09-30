package com.taller.auth.dto;

import com.taller.auth.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Size(min = 3, max = 60) @Pattern(regexp = "^[A-Za-z0-9._-]+$", message = "El usuario solo puede contener letras, numeros, punto, guion o guion bajo.") String username,
        @Email @NotBlank String email,
        @NotNull Role role,
        @NotBlank @Size(min = 8, max = 100) String accessKey,
        @NotBlank @Size(min = 8, max = 100) String password,
        @NotBlank String confirmPassword
) {
}
