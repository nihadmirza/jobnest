package com.example.jobnest.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY) // Use H2 for syntax validation
@TestPropertySource(properties = {
        "spring.sql.init.mode=never",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
})
public class RepositorySyntaxTest {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobSeekerApplyRepository jobSeekerApplyRepository;

    @Test
    public void testJobRepositoryQueries() {
        // Verify query execution returns a non-null list
        List<?> activeJobs = jobRepository.findByActiveTrue();
        assertNotNull(activeJobs);
        assertTrue(activeJobs.isEmpty());

        List<?> searchResults = jobRepository.searchJobs("dev", "ny", "Full-time");
        assertNotNull(searchResults);
        assertEquals(0, searchResults.size());
    }

    @Test
    public void testJobSeekerApplyRepositoryQueries() {
        List<?> recruiterApps = jobSeekerApplyRepository.findByRecruiterId(1);
        assertNotNull(recruiterApps);
        assertTrue(recruiterApps.isEmpty());

        List<?> recruiterJobApps = jobSeekerApplyRepository.findByRecruiterIdAndJobId(1, 1);
        assertNotNull(recruiterJobApps);
        assertEquals(0, recruiterJobApps.size());

        List<?> userApps = jobSeekerApplyRepository.findByUserId(1);
        assertNotNull(userApps);
        assertTrue(userApps.isEmpty());

        assertFalse(jobSeekerApplyRepository.existsByJob_JobIdAndUser_UserId(1, 1));
    }
}
