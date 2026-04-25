package com.shopcart;

import com.shopcart.entity.User;
import com.shopcart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${ADMIN_EMAIL:#{null}}")
    private String adminEmail;

    @Value("${ADMIN_PASSWORD:#{null}}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        if (adminEmail == null || adminPassword == null || adminEmail.isBlank() || adminPassword.isBlank()) {
            log.warn("ADMIN_EMAIL or ADMIN_PASSWORD environment variables are missing! No Master Admin will be created.");
            return;
        }

        // Check if the exact Master Admin already exists
        if (userRepository.findAll().stream().noneMatch(u -> u.getEmail().equals(adminEmail))) {
            log.info("Master Admin not found in database. Seeding Master Admin from environment variables: {}", adminEmail);
            userRepository.save(User.builder()
                    .name("Master Administrator")
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .role(User.Role.admin).build());
            log.info("Master Admin created successfully! Email: {}", adminEmail);
        } else {
            // Optional: You could update the password here if they changed it in Render, but leaving it as is for safety.
            log.info("Master Admin ({}) already exists in the database.", adminEmail);
        }
    }
}
