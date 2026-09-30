package com.taller.auth.service;

import com.taller.auth.dto.AuthResponse;
import com.taller.auth.dto.ForgotPasswordResponse;
import com.taller.auth.dto.LoginRequest;
import com.taller.auth.dto.MessageResponse;
import com.taller.auth.dto.RegisterRequest;
import com.taller.auth.model.Role;
import com.taller.auth.dto.ResetPasswordRequest;
import com.taller.auth.model.User;
import com.taller.auth.repository.UserRepository;
import com.taller.auth.security.CustomUserDetails;
import com.taller.auth.security.JwtService;
import java.security.SecureRandom;
import java.time.Instant;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new IllegalArgumentException("Credenciales invalidas"));
        if (user.getAccessKeyHash() == null || !passwordEncoder.matches(request.accessKey(), user.getAccessKeyHash())) {
            throw new IllegalArgumentException("Credenciales invalidas");
        }
        String token = jwtService.generateToken(new CustomUserDetails(user));

        return new AuthResponse(
                token,
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name()
        );
    }

    @Transactional
    public MessageResponse register(RegisterRequest request) {
        if (!request.password().equals(request.confirmPassword())) {
            throw new IllegalArgumentException("La confirmacion de contrasena no coincide.");
        }
        if (request.role() != Role.GERENTE && request.role() != Role.RECEPCIONISTA) {
            throw new IllegalArgumentException("Solo se permiten los puestos Gerente o Recepcionista.");
        }
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("El usuario ya esta registrado.");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("El correo ya esta registrado.");
        }

        User user = new User();
        user.setName(request.username());
        user.setUsername(request.username());
        user.setEmail(request.email().trim().toLowerCase());
        user.setRole(request.role());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setAccessKeyHash(passwordEncoder.encode(request.accessKey()));
        user.setPasswordChangedAt(Instant.now());
        userRepository.save(user);

        return new MessageResponse("Registro creado correctamente. Ya puedes iniciar sesion.");
    }

    @Transactional
    public ForgotPasswordResponse forgotPassword(String email) {
        String genericMessage = "Si el correo existe, se genero un codigo de recuperacion.";
        return userRepository.findByEmail(email)
                .map(user -> {
                    String code = String.format("%06d", secureRandom.nextInt(1_000_000));
                    user.setResetCodeHash(passwordEncoder.encode(code));
                    user.setResetCodeExpiresAt(Instant.now().plusSeconds(15 * 60));
                    userRepository.save(user);

                    return new ForgotPasswordResponse(genericMessage, code);
                })
                .orElseGet(() -> new ForgotPasswordResponse(genericMessage, null));
    }

    @Transactional
    public MessageResponse resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Codigo o correo invalido"));

        boolean validCode = user.getResetCodeHash() != null
                && user.getResetCodeExpiresAt() != null
                && user.getResetCodeExpiresAt().isAfter(Instant.now())
                && passwordEncoder.matches(request.code(), user.getResetCodeHash());

        if (!validCode) {
            throw new IllegalArgumentException("Codigo o correo invalido");
        }

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        user.setPasswordChangedAt(Instant.now());
        user.setResetCodeHash(null);
        user.setResetCodeExpiresAt(null);
        userRepository.save(user);

        return new MessageResponse("Contrasena actualizada correctamente.");
    }
}
