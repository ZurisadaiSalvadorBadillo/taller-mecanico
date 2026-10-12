package com.taller.auth.config;

import com.taller.auth.model.Role;
import com.taller.auth.model.User;
import com.taller.auth.model.Workshop;
import com.taller.auth.repository.UserRepository;
import com.taller.auth.repository.WorkshopRepository;
import java.time.Instant;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DemoDataSeeder {
    @Bean
    CommandLineRunner seedDemoData(WorkshopRepository workshops, UserRepository users, PasswordEncoder encoder) {
        return args -> {
            for (int number = 1; number <= 5; number++) {
                String name = "Taller Demo " + number;
                if (!workshops.existsWithDuplicateIdentity(name, "taller" + number + "@demo.com", "TDE010101A0" + number)) {
                    Workshop workshop = new Workshop();
                    workshop.setName(name); workshop.setBusinessName("Talleres Demo " + number + " SA de CV");
                    workshop.setRfc("TDE010101A0" + number); workshop.setEmail("taller" + number + "@demo.com");
                    workshop.setPhone("55500000" + number + "0"); workshop.setState("Ciudad de Mexico");
                    workshop.setMunicipality("Benito Juarez"); workshop.setNeighborhood("Del Valle"); workshop.setLocality("Del Valle");
                    workshop.setStreet("Avenida Demo " + number); workshop.setPostalCode("03100");
                    workshop.setPhotoPath("demo-taller-" + number + ".jpg"); workshop.setPhotoContentType("image/jpeg"); workshop.setCreatedAt(Instant.now());
                    workshops.save(workshop);
                }
            }
            for (int number = 1; number <= 10; number++) {
                String username = "usuario" + String.format("%02d", number);
                if (users.existsByUsername(username)) continue;
                User user = new User(); user.setName("Usuario Demo " + number); user.setUsername(username);
                user.setEmail(username + "@taller.com"); user.setRole(number % 2 == 0 ? Role.ADMINISTRADOR : Role.SECRETARIA);
                user.setPasswordHash(encoder.encode("Demo12345")); user.setAccessKeyHash(encoder.encode("Taller2026")); user.setPasswordChangedAt(Instant.now());
                users.save(user);
            }
        };
    }
}
