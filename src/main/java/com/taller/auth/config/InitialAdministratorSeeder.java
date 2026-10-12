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
public class InitialAdministratorSeeder {
    @Bean
    CommandLineRunner seedInitialAdministrator(UserRepository users, PasswordEncoder passwordEncoder) {
        return args -> {
            if (users.existsByUsername("administrador")) return;
            User administrator = new User();
            administrator.setName("Administrador del sistema");
            administrator.setUsername("administrador");
            administrator.setEmail("administrador@taller.com");
            administrator.setRole(Role.ADMINISTRADOR);
            administrator.setPasswordHash(passwordEncoder.encode("Administrador123"));
            administrator.setAccessKeyHash(passwordEncoder.encode("Taller2026"));
            administrator.setPasswordChangedAt(Instant.now());
            users.save(administrator);
        };
    }
}
