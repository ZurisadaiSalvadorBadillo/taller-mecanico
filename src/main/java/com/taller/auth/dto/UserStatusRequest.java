package com.taller.auth.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
public record UserStatusRequest(
        @NotBlank @Pattern(regexp = "ACTIVO|SUSPENDIDO|CANCELADO|ELIMINADO") String value,
        @NotBlank @Size(max = 255) String description) { }
