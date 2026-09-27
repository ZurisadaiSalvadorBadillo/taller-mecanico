package com.taller.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        @Email @NotBlank String email,
        @NotBlank String code,
        @NotBlank @Size(min = 8, message = "La nueva contrasena debe tener al menos 8 caracteres") String newPassword
) {
}
