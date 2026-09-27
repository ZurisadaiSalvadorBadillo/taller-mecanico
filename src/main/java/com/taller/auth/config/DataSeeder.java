package com.taller.auth.config;

import com.taller.auth.model.Role;
import com.taller.auth.model.User;
import com.taller.auth.repository.UserRepository;
import java.time.Instant;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {
    @Bean
    CommandLineRunner seedUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            createUserIfMissing(userRepository, passwordEncoder, "Gerente Principal", "gerente@taller.com", "Gerente123", Role.GERENTE);
            createUserIfMissing(userRepository, passwordEncoder, "Mecanico Taller", "mecanico@taller.com", "Mecanico123", Role.MECANICO);
            createUserIfMissing(userRepository, passwordEncoder, "Cliente Demo", "cliente@taller.com", "Cliente123", Role.CLIENTE);
        };
    }

    private void createUserIfMissing(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            String name,
            String email,
            String password,
            Role role
    ) {
        if (userRepository.existsByEmail(email)) {
            return;
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(role);
        user.setPasswordChangedAt(Instant.now());
        userRepository.save(user);
    }
}
