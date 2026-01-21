package com.example.jobnest.controller;

import com.example.jobnest.entity.Job;
import com.example.jobnest.entity.RecruiterProfile;
import com.example.jobnest.entity.Users;
import com.example.jobnest.entity.UsersType;
import com.example.jobnest.services.AuthenticationService;
import com.example.jobnest.services.JobSeekerApplyService;
import com.example.jobnest.services.JobService;
import com.example.jobnest.services.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class JobControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JobService jobService;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private ProfileService profileService;

    @MockBean
    private JobSeekerApplyService jobSeekerApplyService;

    private Job job;
    private Users user;

    @BeforeEach
    void setUp() {
        job = new Job();
        job.setJobId(1);
        job.setTitle("Java Dev");
        job.setActive(true);

        user = new Users();
        user.setUserId(1);
        user.setEmail("test@test.com");
    }

    @Test
    @WithMockUser
    void listAllJobs_ShouldReturnJobsView() throws Exception {
        when(jobService.findAllActiveJobs()).thenReturn(Arrays.asList(job));

        mockMvc.perform(get("/jobs"))
                .andExpect(status().isOk())
                .andExpect(view().name("jobs-list"))
                .andExpect(model().attributeExists("jobs"));
    }

    @Test
    @WithMockUser
    void globalSearch_ShouldReturnJobsView() throws Exception {
        when(jobService.searchJobs(anyString(), any(), any())).thenReturn(Arrays.asList(job));

        mockMvc.perform(get("/global-search").param("job", "Java"))
                .andExpect(status().isOk())
                .andExpect(view().name("jobs-list"))
                .andExpect(model().attributeExists("jobs"));
    }

    @Test
    void viewJob_ShouldReturnJobDetail_WhenFound() throws Exception {
        when(jobService.findById(1)).thenReturn(Optional.of(job));
        when(authenticationService.getCurrentAuthenticatedUser()).thenReturn(null); // Anonymous

        mockMvc.perform(get("/jobs/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("job-details"))
                .andExpect(model().attributeExists("job"));
    }

    @Test
    void viewJob_ShouldRedirect_WhenNotFound() throws Exception {
        when(jobService.findById(99)).thenReturn(Optional.empty());

        mockMvc.perform(get("/jobs/99"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/jobs"));
    }

    @Test
    void listRecruiterJobs_ShouldReturnRecruiterJobs() throws Exception {
        // Mock Authenticated Recruiter
        Users recruiter = new Users();
        recruiter.setUserId(2);

        RecruiterProfile profile = new RecruiterProfile(recruiter);
        profile.setUserAccountId(200);

        when(authenticationService.getCurrentAuthenticatedUser()).thenReturn(recruiter);
        when(profileService.getRecruiterProfile(2)).thenReturn(profile);
        when(jobService.findJobsByRecruiterId(200)).thenReturn(Arrays.asList(job));

        mockMvc.perform(get("/recruiter/jobs"))
                .andExpect(status().isOk())
                .andExpect(view().name("recruiter-jobs-list"))
                .andExpect(model().attributeExists("jobs"));
    }

    @Test
    void createJob_ShouldRedirectToPricing() throws Exception {
        // Mock Authenticated Recruiter
        Users recruiter = new Users();
        recruiter.setUserId(2);
        RecruiterProfile profile = new RecruiterProfile(recruiter);

        when(authenticationService.getCurrentAuthenticatedUser()).thenReturn(recruiter);
        when(profileService.getRecruiterProfile(2)).thenReturn(profile);
        when(jobService.save(any(Job.class))).thenAnswer(invocation -> {
            Job j = invocation.getArgument(0);
            j.setJobId(123);
            return j;
        });

        mockMvc.perform(post("/recruiter/jobs")
                .param("title", "New Job")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/recruiter/pricing?jobId=123"));
    }

    @Test
    void updateJob_ShouldUpdateAndRedirect() throws Exception {
        when(jobService.findById(1)).thenReturn(Optional.of(job));
        when(jobService.update(any(Job.class))).thenReturn(job);

        mockMvc.perform(post("/recruiter/jobs/1")
                .param("title", "Updated Title")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/recruiter/jobs"));
    }

    @Test
    void deleteJob_ShouldDeleteAndRedirect() throws Exception {
        // doNothing().when(jobService).deleteById(1); // implicit void

        mockMvc.perform(post("/recruiter/jobs/1/delete")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/recruiter/jobs"));
    }

    @Test
    void archiveJob_ShouldArchiveAndRedirect() throws Exception {
        mockMvc.perform(post("/recruiter/jobs/1/archive")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/recruiter/jobs"));
    }

    @Test
    void applyToJob_ShouldSubmitApplication_WhenJobSeeker() throws Exception {
        Users seeker = new Users();
        seeker.setUserId(3);
        UsersType type = new UsersType();
        type.setUserTypeId(2); // Job Seeker
        seeker.setUserTypeId(type);

        when(authenticationService.getCurrentAuthenticatedUser()).thenReturn(seeker);
        when(authenticationService.hasRole("JOB SEEKER")).thenReturn(true);
        // jobSeekerApplyService.applyToJob is void

        mockMvc.perform(post("/jobs/1/apply")
                .param("coverLetter", "Hello")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/jobs/1"));
    }

    @Test
    void applyToJob_ShouldRedirectToLogin_WhenNotAuthenticated() throws Exception {
        when(authenticationService.getCurrentAuthenticatedUser()).thenReturn(null);

        mockMvc.perform(post("/jobs/1/apply")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
}
