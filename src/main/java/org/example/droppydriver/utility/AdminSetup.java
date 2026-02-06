package org.example.droppydriver.utility;

import lombok.extern.slf4j.Slf4j;
import org.example.droppydriver.models.Role;
import org.example.droppydriver.models.User;
import org.example.droppydriver.repository.IUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Slf4j
public class AdminSetup {

    @Bean
    CommandLineRunner initAdmin(IUserRepository repository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (!repository.existsUserByEmail("admin@droppydriver.com")) {
                var admin = new User(
                        "admin",
                        passwordEncoder.encode("admin123"),
                        "admin@droppydriver.com",
                        22,
                        Role.ADMIN
                );
                repository.save(admin);
                log.info("Default Admin Created: {} / admin123", admin.getEmail());
            }
        };
    }
}
