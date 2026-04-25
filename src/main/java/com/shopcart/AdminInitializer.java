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

    @Value("${ADMIN_EMAIL:admin@shopcart.com}")
    private String adminEmail;

    @Value("${ADMIN_PASSWORD:admin}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        if (userRepository.findAll().stream().noneMatch(u -> u.getEmail().equals(adminEmail))) {
            log.info("Default admin not found. Seeding Master Admin: {}", adminEmail);
            userRepository.save(User.builder()
                    .name("Master Administrator")
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .role(User.Role.admin).build());
            log.info("Master Admin created successfully. You can log in with Email: {} and Password: {}", adminEmail, adminPassword);
        } else {
            log.info("Default Master Admin ({}) already exists.", adminEmail);
        }
    }
}
