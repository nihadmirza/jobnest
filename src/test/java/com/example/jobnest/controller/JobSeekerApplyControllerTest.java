package com.example.jobnest.controller;

import com.example.jobnest.dto.response.RecruiterApplicationStatsDTO;
import com.example.jobnest.entity.Job;
import com.example.jobnest.entity.JobSeekerApply;
import com.example.jobnest.entity.JobSeekerProfile;
import com.example.jobnest.entity.RecruiterProfile;
import com.example.jobnest.entity.Users;
import com.example.jobnest.services.ApplicationManagementService;
import com.example.jobnest.services.AuthenticationService;
import com.example.jobnest.services.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class JobSeekerApplyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ApplicationManagementService applicationManagementService;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private ProfileService profileService;

    private Users recruiterUser;
    private RecruiterProfile recruiterProfile;
    private RecruiterApplicationStatsDTO statsDTO;
    private JobSeekerApply jobSeekerApply;
    private JobSeekerProfile jobSeekerProfile;

    @BeforeEach
    void setUp() {
        recruiterUser = new Users();
        recruiterUser.setUserId(1);
        recruiterUser.setEmail("recruiter@example.com");

        recruiterProfile = new RecruiterProfile(recruiterUser);
        recruiterProfile.setUserAccountId(100);

        statsDTO = new RecruiterApplicationStatsDTO();
        statsDTO.setApplications(Collections.emptyList());
        statsDTO.setCandidateProfilesMap(Collections.emptyMap());
        statsDTO.setAllJobs(Collections.emptyList());
        statsDTO.setTotalApplications(0L);
        statsDTO.setPendingCount(0L);
        statsDTO.setReviewedCount(0L);
        statsDTO.setAcceptedCount(0L);
        statsDTO.setRejectedCount(0L);

        Job job = new Job();
        job.setJobId(10);
        job.setTitle("Test Job");

        Users candidateUser = new Users();
        candidateUser.setUserId(2);

        jobSeekerApply = new JobSeekerApply();
        jobSeekerApply.setApplyId(1);
        jobSeekerApply.setJob(job);
        jobSeekerApply.setUser(candidateUser);
        jobSeekerApply.setApplyDate(new java.util.Date());
        jobSeekerApply.setStatus("Pending");

        jobSeekerProfile = new JobSeekerProfile(candidateUser);
        jobSeekerProfile.setUserAccountId(200);
    }

    @Test
    @WithMockUser
    void viewApplications_ShouldReturnViewWithData() throws Exception {
        when(authenticationService.getCurrentAuthenticatedUser()).thenReturn(recruiterUser);
        when(profileService.getRecruiterProfile(1)).thenReturn(recruiterProfile);
        when(applicationManagementService.getApplicationsWithStats(anyInt(), any(), any())).thenReturn(statsDTO);

        mockMvc.perform(get("/recruiter/applications"))
                .andExpect(status().isOk())
                .andExpect(view().name("recruiter-applications"))
                .andExpect(model().attributeExists("applications"))
                .andExpect(model().attributeExists("totalApplications"));
    }

    @Test
    @WithMockUser
    void updateApplicationStatus_ShouldRedirect() throws Exception {
        when(authenticationService.getCurrentAuthenticatedUser()).thenReturn(recruiterUser);
        when(profileService.getRecruiterProfile(1)).thenReturn(recruiterProfile);
        doNothing().when(applicationManagementService).updateApplicationStatus(anyInt(), anyString(), anyInt());

        mockMvc.perform(post("/recruiter/applications/1/status")
                .param("status", "Accepted")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/recruiter/applications"));
    }

    @Test
    @WithMockUser
    void viewCandidateProfile_ShouldReturnView() throws Exception {
        when(authenticationService.getCurrentAuthenticatedUser()).thenReturn(recruiterUser);
        when(profileService.getRecruiterProfile(1)).thenReturn(recruiterProfile);
        when(applicationManagementService.getApplicationForRecruiter(anyInt(), anyInt())).thenReturn(jobSeekerApply);
        when(profileService.getJobSeekerProfile(2)).thenReturn(jobSeekerProfile);

        mockMvc.perform(get("/recruiter/applications/1/candidate-profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("candidate-profile-view"))
                .andExpect(model().attributeExists("candidateProfile"));
    }
}
