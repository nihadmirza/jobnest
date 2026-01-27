package com.example.jobnest.controller;

import com.example.jobnest.common.UserType;
import com.example.jobnest.dto.response.UnifiedDashboardDTO;
import com.example.jobnest.entity.JobSeekerProfile;
import com.example.jobnest.entity.RecruiterProfile;
import com.example.jobnest.exception.UnauthorizedException;
import com.example.jobnest.services.DashboardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(DashboardController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser
@SuppressWarnings("null")
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DashboardService dashboardService;

    @Test
    void dashboard_redirectsWhenUserMissing() throws Exception {
        when(dashboardService.getDashboardData()).thenThrow(new UnauthorizedException("Authentication required"));

        mockMvc.perform(get("/dashboard").with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void dashboard_rendersRecruiterDashboard() throws Exception {
        RecruiterProfile profile = new RecruiterProfile();
        UnifiedDashboardDTO dto = UnifiedDashboardDTO.builder()
                .userType(UserType.RECRUITER)
                .recruiterProfile(profile)
                .applicationCount(3L)
                .pendingApplicationCount(1L)
                .build();
        when(dashboardService.getDashboardData()).thenReturn(dto);

        mockMvc.perform(get("/dashboard").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(model().attribute("dashboardData", dto));
    }

    @Test
    void dashboard_rendersJobSeekerDashboard() throws Exception {
        JobSeekerProfile profile = new JobSeekerProfile();
        UnifiedDashboardDTO dto = UnifiedDashboardDTO.builder()
                .userType(UserType.JOB_SEEKER)
                .jobSeekerProfile(profile)
                .applications(List.of())
                .build();
        when(dashboardService.getDashboardData()).thenReturn(dto);

        mockMvc.perform(get("/dashboard").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(model().attribute("dashboardData", dto));
    }
}
