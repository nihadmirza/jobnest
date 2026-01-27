package com.example.jobnest.integration;

import com.example.jobnest.dto.request.UserRegistrationRequest;
import com.example.jobnest.services.UsersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@SuppressWarnings("null")
public class RecruiterLoginTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsersService usersService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setup() {
        // Ensure UsersType IDs exist (tests run with H2 + spring.sql.init.mode=never)
        // 'MERGE' works in H2 to upsert by primary key
        jdbcTemplate.update("MERGE INTO users_type (user_type_id, user_type_name) KEY(user_type_id) VALUES (1, 'Recruiter')");
        jdbcTemplate.update("MERGE INTO users_type (user_type_id, user_type_name) KEY(user_type_id) VALUES (2, 'Job Seeker')");
        jdbcTemplate.update("MERGE INTO users_type (user_type_id, user_type_name) KEY(user_type_id) VALUES (3, 'Admin')");
    }

    @Test
    public void testRecruiterLoginWithCaseMismatch() throws Exception {
        // 1. Register with Mixed Case
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setFirstName("Test");
        request.setLastName("Recruiter");
        request.setEmail("MixedCase@Example.com");
        request.setPassword("password123");
        request.setCompany("Test Corp");
        request.setRole("RECRUITER");

        usersService.registerUser(request);

        // 2. Attempt Login with Lower Case (Typical User Behavior)
        // Should succeed now that we normalize emails
        mockMvc.perform(formLogin("/login")
                .user("username", "mixedcase@example.com")
                .password("password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard")) // Should succeed with normalization
                .andExpect(authenticated().withRoles("RECRUITER"));
    }

    @Test
    public void testRecruiterRoleLocaleIssue() throws Exception {
        // Set JVM default locale to Azerbaijani to test "i".toUpperCase() -> "İ"
        java.util.Locale original = java.util.Locale.getDefault();
        try {
            java.util.Locale.setDefault(new java.util.Locale("az", "AZ"));

            UserRegistrationRequest request = new UserRegistrationRequest();
            request.setFirstName("Azeri");
            request.setLastName("User");
            request.setEmail("azeri@example.com");
            request.setPassword("password123");
            request.setCompany("Azeri Corp");
            request.setRole("RECRUITER"); // "Recruiter" in DB

            usersService.registerUser(request);

            mockMvc.perform(formLogin("/login")
                    .user("username", "azeri@example.com")
                    .password("password123"))
                    .andExpect(authenticated().withRoles("RECRUITER")); // Will fail if it becomes "RECRUİTER"
        } finally {
            java.util.Locale.setDefault(original);
        }
    }
}
