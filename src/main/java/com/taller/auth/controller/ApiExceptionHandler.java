package com.taller.auth.controller;

import com.taller.auth.dto.MessageResponse;
import com.taller.auth.service.DuplicateClientException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<MessageResponse> handleBadCredentials() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new MessageResponse("Credenciales invalidas"));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<MessageResponse> handleIllegalArgument(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(new MessageResponse(exception.getMessage()));
    }

    @ExceptionHandler({DuplicateClientException.class, DataIntegrityViolationException.class})
    public ResponseEntity<MessageResponse> handleDuplicateClient() {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new MessageResponse("Ya existe un cliente con datos de contacto registrados."));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MessageResponse> handleValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage() == null ? "Datos invalidos" : error.getDefaultMessage())
                .orElse("Datos invalidos");
        return ResponseEntity.badRequest().body(new MessageResponse(message));
    }
}
