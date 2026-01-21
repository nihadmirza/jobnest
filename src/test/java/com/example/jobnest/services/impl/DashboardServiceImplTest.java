package com.example.jobnest.services.impl;

import com.example.jobnest.dto.response.JobSeekerDashboardDTO;
import com.example.jobnest.dto.response.RecruiterDashboardDTO;
import com.example.jobnest.entity.JobSeekerProfile;
import com.example.jobnest.entity.RecruiterProfile;
import com.example.jobnest.exception.ResourceNotFoundException;
import com.example.jobnest.repository.JobSeekerProfileRepository;
import com.example.jobnest.repository.RecruiterProfileRepository;
import com.example.jobnest.services.JobSeekerApplyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {

    @Mock
    private RecruiterProfileRepository recruiterProfileRepository;

    @Mock
    private JobSeekerProfileRepository jobSeekerProfileRepository;

    @Mock
    private JobSeekerApplyService jobSeekerApplyService;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    @Test
    void getRecruiterDashboardData_returnsStats() {
        RecruiterProfile profile = new RecruiterProfile();
        profile.setUserAccountId(42);
        when(recruiterProfileRepository.findById(1)).thenReturn(Optional.of(profile));
        when(jobSeekerApplyService.getApplicationCountByRecruiterId(42)).thenReturn(10L);
        when(jobSeekerApplyService.getApplicationCountByRecruiterIdAndStatus(42, "Pending")).thenReturn(3L);

        RecruiterDashboardDTO dto = dashboardService.getRecruiterDashboardData(1);

        assertEquals(profile, dto.getProfile());
        assertEquals(10L, dto.getApplicationCount());
        assertEquals(3L, dto.getPendingApplicationCount());
    }

    @Test
    void getRecruiterDashboardData_throwsWhenProfileMissing() {
        when(recruiterProfileRepository.findById(99)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> dashboardService.getRecruiterDashboardData(99));
    }

    @Test
    void getJobSeekerDashboardData_returnsProfileAndApplications() {
        JobSeekerProfile profile = new JobSeekerProfile();
        profile.setSkills(Collections.emptyList());
        when(jobSeekerProfileRepository.findById(2)).thenReturn(Optional.of(profile));
        when(jobSeekerApplyService.getApplicationsByUserId(2)).thenReturn(Collections.emptyList());

        JobSeekerDashboardDTO dto = dashboardService.getJobSeekerDashboardData(2);

        assertEquals(profile, dto.getProfile());
        assertEquals(0, dto.getApplications().size());
    }
}
