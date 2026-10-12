package com.taller.auth.service;

import com.taller.auth.dto.MessageResponse;
import com.taller.auth.dto.RegisterRequest;
import com.taller.auth.model.Role;
import com.taller.auth.model.User;
import com.taller.auth.repository.UserRepository;
import java.time.Instant;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public MessageResponse createUser(RegisterRequest request) {
        if (!request.password().equals(request.confirmPassword())) {
            throw new IllegalArgumentException("La confirmacion de contrasena no coincide.");
        }
        if (request.role() != Role.ADMINISTRADOR && request.role() != Role.SECRETARIA) {
            throw new IllegalArgumentException("Solo se permiten los puestos Administrador o Secretaria.");
        }
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("El usuario ya esta registrado");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("El correo ya esta registrado");
        }

        User user = new User();
        user.setName(request.username());
        user.setUsername(request.username());
        user.setEmail(request.email().trim().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setAccessKeyHash(passwordEncoder.encode(request.accessKey()));
        user.setRole(request.role());
        user.setPasswordChangedAt(Instant.now());
        userRepository.save(user);

        return new MessageResponse("Usuario creado correctamente.");
    }

}
