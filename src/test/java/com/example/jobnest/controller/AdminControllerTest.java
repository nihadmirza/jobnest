package com.example.jobnest.controller;

import com.example.jobnest.dto.response.AdminDashboardDTO;
import com.example.jobnest.dto.response.AdminReportsDTO;
import com.example.jobnest.services.AdminReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@SuppressWarnings("null")
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminReportService adminReportService;

    private AdminDashboardDTO dashboardDTO;
    private AdminReportsDTO reportsDTO;

    @BeforeEach
    void setUp() {
        dashboardDTO = new AdminDashboardDTO();
        dashboardDTO.setTotalUsers(100L);
        dashboardDTO.setActiveJobs(50L);
        dashboardDTO.setTotalApplications(200L);

        reportsDTO = new AdminReportsDTO();
        reportsDTO.setTotalUsers(100L);
        reportsDTO.setRecruiters(20L);
        reportsDTO.setJobSeekers(80L);
        reportsDTO.setTotalJobs(60L);
        reportsDTO.setActiveJobs(50L);
        reportsDTO.setTotalApplications(200L);
        reportsDTO.setAvgApplications("4.0");
        reportsDTO.setTopJobs(Collections.emptyList());
        reportsDTO.setTopRecruiters(Collections.emptyList());
        reportsDTO.setTopJobSeekers(Collections.emptyList());
    }

    @Test
    void adminLogin_ShouldReturnLoginView() throws Exception {
        mockMvc.perform(get("/admin/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-login"));
    }

    @Test
    void adminLogin_ShouldShowError_WhenErrorParamPresent() throws Exception {
        mockMvc.perform(get("/admin/login").param("error", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-login"))
                .andExpect(content().string(containsString("Invalid credentials")));
    }

    @Test
    void adminLogin_ShouldShowBlocked_WhenBlockedParamPresent() throws Exception {
        mockMvc.perform(get("/admin/login").param("blocked", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-login"))
                .andExpect(content().string(containsString("Account blocked for 15 minutes")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void dashboard_ShouldReturnDashboardData() throws Exception {
        when(adminReportService.getDashboardStats()).thenReturn(dashboardDTO);

        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-dashboard"))
                .andExpect(model().attributeExists("dashboardData"))
                .andExpect(model().attribute("dashboardData", hasProperty("totalUsers", is(100L))))
                .andExpect(model().attribute("dashboardData", hasProperty("activeJobs", is(50L))));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void dashboard_ShouldReturnErrorView_OnException() throws Exception {
        when(adminReportService.getDashboardStats()).thenThrow(new RuntimeException("DB Error"));

        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void reports_ShouldReturnReportsData() throws Exception {
        when(adminReportService.getDetailedReports()).thenReturn(reportsDTO);

        mockMvc.perform(get("/admin/reports"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-reports"))
                .andExpect(model().attributeExists("reportsData"))
                .andExpect(model().attribute("reportsData", hasProperty("totalUsers", is(100L))));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void reports_ShouldReturnErrorView_OnException() throws Exception {
        when(adminReportService.getDetailedReports()).thenThrow(new RuntimeException("Report Error"));

        mockMvc.perform(get("/admin/reports"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("error"));
    }
}
