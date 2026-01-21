package com.example.jobnest.config;

import com.example.jobnest.entity.Users;
import com.example.jobnest.entity.UsersType;
import com.example.jobnest.repository.UsersRepository;
import com.example.jobnest.repository.UsersTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "app.admin.auto-create", havingValue = "true", matchIfMissing = true)
public class AdminUserInitializer implements CommandLineRunner {

    private final UsersRepository usersRepository;
    private final UsersTypeRepository usersTypeRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String ADMIN_EMAIL = "admin@jobnest.com";
    @org.springframework.beans.factory.annotation.Value("${app.admin.password:Admin@2024}")
    private String adminPassword;
    private static final int ADMIN_TYPE_ID = 3;

    @Override
    public void run(String... args) throws Exception {
        log.info("Checking for admin user initialization...");

        // Check if admin user already exists
        Optional<Users> existingAdmin = usersRepository.findByEmail(ADMIN_EMAIL);

        if (existingAdmin.isPresent()) {
            log.info("Admin user already exists: {}", ADMIN_EMAIL);
            return;
        }

        // Get Admin user type
        Optional<UsersType> adminType = usersTypeRepository.findById(ADMIN_TYPE_ID);

        if (adminType.isEmpty()) {
            log.error("Admin user type (ID: {}) not found in database. Please ensure data.sql has been executed.",
                    ADMIN_TYPE_ID);
            return;
        }

        // Create admin user
        Users adminUser = new Users();
        adminUser.setEmail(ADMIN_EMAIL);
        adminUser.setPassword(passwordEncoder.encode(adminPassword));
        adminUser.setUserTypeId(adminType.get());
        adminUser.setActive(true);
        adminUser.setRegistrationDate(new Date(System.currentTimeMillis()));

        usersRepository.save(adminUser);

        log.info("✅ Admin user created successfully!");
        log.info("   Email: {}", ADMIN_EMAIL);
        log.info("   Password: {}", adminPassword);
        log.info("   Please change the password after first login for security.");
        log.info("   To disable auto-creation, set 'app.admin.auto-create=false' in application.properties");
    }
}
