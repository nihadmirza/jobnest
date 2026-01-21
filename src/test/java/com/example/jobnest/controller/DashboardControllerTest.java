package com.example.jobnest.controller;

import com.example.jobnest.dto.response.JobSeekerDashboardDTO;
import com.example.jobnest.dto.response.RecruiterDashboardDTO;
import com.example.jobnest.entity.JobSeekerProfile;
import com.example.jobnest.entity.RecruiterProfile;
import com.example.jobnest.entity.Users;
import com.example.jobnest.entity.UsersType;
import com.example.jobnest.services.AuthenticationService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(DashboardController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DashboardService dashboardService;

    @MockBean
    private AuthenticationService authenticationService;

    @Test
    void dashboard_redirectsWhenUserMissing() throws Exception {
        when(authenticationService.getCurrentAuthenticatedUser()).thenReturn(null);

        mockMvc.perform(get("/dashboard").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error=true"));
    }

    @Test
    void dashboard_rendersRecruiterDashboard() throws Exception {
        UsersType recruiterType = new UsersType();
        recruiterType.setUserTypeId(1);
        Users user = new Users();
        user.setUserId(10);
        user.setUserTypeId(recruiterType);
        when(authenticationService.getCurrentAuthenticatedUser()).thenReturn(user);

        RecruiterProfile profile = new RecruiterProfile();
        RecruiterDashboardDTO dto = RecruiterDashboardDTO.builder()
                .profile(profile)
                .applicationCount(3)
                .pendingApplicationCount(1)
                .build();
        when(dashboardService.getRecruiterDashboardData(10)).thenReturn(dto);

        mockMvc.perform(get("/dashboard").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(model().attribute("profile", profile))
                .andExpect(model().attribute("user", profile))
                .andExpect(model().attribute("userType", "RECRUITER"))
                .andExpect(model().attribute("applicationCount", 3L))
                .andExpect(model().attribute("pendingApplicationCount", 1L));
    }

    @Test
    void dashboard_rendersJobSeekerDashboard() throws Exception {
        UsersType seekerType = new UsersType();
        seekerType.setUserTypeId(2);
        Users user = new Users();
        user.setUserId(15);
        user.setUserTypeId(seekerType);
        when(authenticationService.getCurrentAuthenticatedUser()).thenReturn(user);

        JobSeekerProfile profile = new JobSeekerProfile();
        JobSeekerDashboardDTO dto = JobSeekerDashboardDTO.builder()
                .profile(profile)
                .applications(List.of())
                .build();
        when(dashboardService.getJobSeekerDashboardData(15)).thenReturn(dto);

        mockMvc.perform(get("/dashboard").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(model().attribute("profile", profile))
                .andExpect(model().attribute("user", profile))
                .andExpect(model().attribute("userType", "JOB_SEEKER"))
                .andExpect(model().attribute("applications", List.of()));
    }
}
