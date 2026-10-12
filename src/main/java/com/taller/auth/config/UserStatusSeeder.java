package com.taller.auth.config;

import com.taller.auth.model.UserStatus;
import com.taller.auth.repository.UserStatusRepository;
import java.time.Instant;
import java.util.Map;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserStatusSeeder {
    @Bean CommandLineRunner seedUserStatuses(UserStatusRepository repository) {
        return args -> Map.of("ACTIVO", "Cliente habilitado para operacion.", "SUSPENDIDO", "Cliente temporalmente suspendido.", "CANCELADO", "Cliente cancelado.", "ELIMINADO", "Registro eliminado logicamente.")
                .forEach((value, description) -> repository.findByValue(value).orElseGet(() -> { UserStatus status = new UserStatus(); status.setValue(value); status.setDescription(description); status.setActualizado(Instant.now()); return repository.save(status); }));
    }
}
