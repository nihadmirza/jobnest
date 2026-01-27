package com.example.jobnest.controller;

import com.example.jobnest.dto.response.CandidateProfilePageDTO;
import com.example.jobnest.dto.response.RecruiterApplicationStatsDTO;
import com.example.jobnest.dto.response.RecruiterApplicationsPageDTO;
import com.example.jobnest.entity.Job;
import com.example.jobnest.entity.JobSeekerApply;
import com.example.jobnest.entity.JobSeekerProfile;
import com.example.jobnest.entity.Users;
import com.example.jobnest.services.RecruiterApplicationsPageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
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
    private RecruiterApplicationsPageService recruiterApplicationsPageService;

    private Users recruiterUser;
    private RecruiterApplicationStatsDTO statsDTO;
    private JobSeekerApply jobSeekerApply;
    private JobSeekerProfile jobSeekerProfile;

    @BeforeEach
    void setUp() {
        recruiterUser = new Users();
        recruiterUser.setUserId(1);
        recruiterUser.setEmail("recruiter@example.com");

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
        RecruiterApplicationsPageDTO page = RecruiterApplicationsPageDTO.builder()
                .stats(statsDTO)
                .selectedJobId(null)
                .selectedStatus("All")
                .build();
        when(recruiterApplicationsPageService.viewApplications(any(), any()))
                .thenReturn(new RecruiterApplicationsPageService.PageResult("recruiter-applications", Map.of("page", page)));

        mockMvc.perform(get("/recruiter/applications"))
                .andExpect(status().isOk())
                .andExpect(view().name("recruiter-applications"))
                .andExpect(model().attributeExists("page"));
    }

    @Test
    @WithMockUser
    void updateApplicationStatus_ShouldRedirect() throws Exception {
        when(recruiterApplicationsPageService.updateApplicationStatus(anyInt(), anyString(), any(), any()))
                .thenReturn(new RecruiterApplicationsPageService.ActionResult(
                        "redirect:/recruiter/applications",
                        "success",
                        "Application status updated successfully"
                ));

        mockMvc.perform(post("/recruiter/applications/1/status")
                .param("status", "Accepted")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/recruiter/applications"))
                .andExpect(flash().attributeExists("success"));
    }

    @Test
    @WithMockUser
    void viewCandidateProfile_ShouldReturnView() throws Exception {
        CandidateProfilePageDTO page = CandidateProfilePageDTO.builder()
                .application(jobSeekerApply)
                .candidateProfile(jobSeekerProfile)
                .candidateUser(jobSeekerApply.getUser())
                .job(jobSeekerApply.getJob())
                .build();
        when(recruiterApplicationsPageService.viewCandidateProfile(anyInt()))
                .thenReturn(new RecruiterApplicationsPageService.PageResult("candidate-profile-view", Map.of("page", page)));

        mockMvc.perform(get("/recruiter/applications/1/candidate-profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("candidate-profile-view"))
                .andExpect(model().attributeExists("page"));
    }
}
