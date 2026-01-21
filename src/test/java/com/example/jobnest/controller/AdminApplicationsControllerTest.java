package com.example.jobnest.controller;

import com.example.jobnest.entity.Job;
import com.example.jobnest.entity.JobSeekerApply;
import com.example.jobnest.entity.RecruiterProfile;
import com.example.jobnest.entity.Users;
import com.example.jobnest.services.JobSeekerApplyService;
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

@WebMvcTest(AdminApplicationsController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser
class AdminApplicationsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JobSeekerApplyService jobSeekerApplyService;

    @Test
    void listAllApplications_returnsViewOnSuccess() throws Exception {
        Users user = new Users();
        user.setUserId(7);
        user.setEmail("applicant@example.com");
        RecruiterProfile recruiter = new RecruiterProfile();
        recruiter.setCompany("ACME");
        Job job = new Job();
        job.setJobId(50);
        job.setTitle("QA Engineer");
        job.setRecruiter(recruiter);
        JobSeekerApply application = new JobSeekerApply();
        application.setApplyId(1);
        application.setUser(user);
        application.setJob(job);
        application.setApplyDate(new Date());
        application.setStatus("Pending");
        List<JobSeekerApply> applications = List.of(application);
        when(jobSeekerApplyService.getAllApplications()).thenReturn(applications);

        mockMvc.perform(get("/admin/applications").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-applications"))
                .andExpect(model().attribute("applications", applications));
    }

    @Test
    void listAllApplications_returnsErrorViewOnException() throws Exception {
        when(jobSeekerApplyService.getAllApplications()).thenThrow(new RuntimeException("DB down"));

        mockMvc.perform(get("/admin/applications").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("error"));
    }
}
