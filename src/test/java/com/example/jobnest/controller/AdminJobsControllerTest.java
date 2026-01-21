package com.example.jobnest.controller;

import com.example.jobnest.entity.Job;
import com.example.jobnest.entity.RecruiterProfile;
import com.example.jobnest.services.JobService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(AdminJobsController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser
class AdminJobsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JobService jobService;

    @Test
    void listAllJobs_returnsViewOnSuccess() throws Exception {
        RecruiterProfile recruiter = new RecruiterProfile();
        recruiter.setCompany("ACME");
        Job job = new Job();
        job.setJobId(101);
        job.setTitle("Software Engineer");
        job.setLocation("Baku");
        job.setPostedDate(new Date());
        job.setActive(true);
        job.setRecruiter(recruiter);
        List<Job> jobs = List.of(job);
        when(jobService.findAllJobs()).thenReturn(jobs);

        mockMvc.perform(get("/admin/jobs").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-jobs"))
                .andExpect(model().attribute("jobs", jobs));
    }

    @Test
    void listAllJobs_returnsErrorViewOnException() throws Exception {
        when(jobService.findAllJobs()).thenThrow(new RuntimeException("DB down"));

        mockMvc.perform(get("/admin/jobs").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("error"));
    }
}
