package com.taller.auth.service;

import com.taller.auth.dto.MessageResponse;
import com.taller.auth.dto.PublicRegisterRequest;
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
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("El correo ya esta registrado");
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(request.role());
        user.setPasswordChangedAt(Instant.now());
        userRepository.save(user);

        return new MessageResponse("Usuario creado correctamente.");
    }

    @Transactional
    public MessageResponse registerClient(PublicRegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("El correo ya esta registrado");
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(Role.CLIENTE);
        user.setPasswordChangedAt(Instant.now());
        userRepository.save(user);

        return new MessageResponse("Registro completado correctamente.");
    }
}
