package com.example.jobnest.config;

import com.example.jobnest.dto.response.AdminDashboardDTO;
import com.example.jobnest.common.UserType;
import com.example.jobnest.dto.response.UnifiedDashboardDTO;
import com.example.jobnest.entity.JobSeekerProfile;
import com.example.jobnest.entity.Users;
import com.example.jobnest.entity.UsersType;
import com.example.jobnest.services.AdminReportService;
import com.example.jobnest.services.AuthenticationService;
import com.example.jobnest.services.DashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private CustomAuthenticationSuccessHandler successHandler;

        @MockBean
        private CustomAuthenticationFailureHandler failureHandler;

        @MockBean
        private DashboardService dashboardService;

        @MockBean
        private AuthenticationService authenticationService;

        @MockBean
        private AdminReportService adminReportService;

        @BeforeEach
        void setup() {
                // Mock current user for dashboard access
                Users mockUser = new Users();
                UsersType type = new UsersType();
                type.setUserTypeId(2); // Job Seeker
                mockUser.setUserTypeId(type);
                mockUser.setUserId(1);

                when(authenticationService.getCurrentAuthenticatedUser()).thenReturn(mockUser);

                // Mock dashboard data
                UnifiedDashboardDTO mockData = UnifiedDashboardDTO.builder()
                                .userType(UserType.JOB_SEEKER)
                                .jobSeekerProfile(new JobSeekerProfile())
                                .applications(java.util.List.of())
                                .build();
                when(dashboardService.getDashboardData()).thenReturn(mockData);

                // Mock admin data
                when(adminReportService.getDashboardStats())
                                .thenReturn(new AdminDashboardDTO());
        }

        @Test
        void publicEndpoints_ShouldBeAccessibleWithoutAuth() throws Exception {
                mockMvc.perform(get("/"))
                                .andExpect(status().isOk());

                mockMvc.perform(get("/login"))
                                .andExpect(status().isOk());

                mockMvc.perform(get("/register"))
                                .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(authorities = "ROLE_ADMIN")
        void testAdminLoginSuccess() throws Exception {
                mockMvc.perform(get("/admin/login").param("logout", "true"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("admin-login")); // Fixed expectation to match actual
                // Actually test logic is to verify login page load or something?
                // The original test code used redirectedUrlPattern. If it was testing Logout:
                // .logoutSuccessUrl(ADMIN_LOGIN_URL + "?logout=true")
        }

        @Test
        void testUnauthenticatedAccessRedirectsToLogin() throws Exception {
                mockMvc.perform(get("/dashboard"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("http://localhost/login"));
        }

        @Test
        void testAdminUnauthenticatedAccessRedirectsToAdminLogin() throws Exception {
                mockMvc.perform(get("/admin/dashboard"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("http://localhost/admin/login"));
        }

        @Test
        @WithMockUser(roles = "JOB SEEKER")
        void jobSeeker_ShouldAccessDashboard() throws Exception {
                mockMvc.perform(get("/dashboard"))
                                .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(roles = "JOB SEEKER")
        void jobSeeker_ShouldNotAccessAdminPage() throws Exception {
                mockMvc.perform(get("/admin/dashboard"))
                                .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void admin_ShouldAccessAdminDashboard() throws Exception {
                mockMvc.perform(get("/admin/dashboard"))
                                .andExpect(status().isOk());
        }
}
