package com.taller.auth.dto;

public record ForgotPasswordResponse(
        String message,
        String developmentResetCode
) {
}
