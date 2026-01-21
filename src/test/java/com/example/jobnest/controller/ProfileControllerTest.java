package com.example.jobnest.controller;

import com.example.jobnest.entity.Users;
import com.example.jobnest.entity.UsersType;
import com.example.jobnest.entity.JobSeekerProfile;
import com.example.jobnest.entity.RecruiterProfile;
import com.example.jobnest.services.AuthenticationService;
import com.example.jobnest.services.ProfileService;
import com.example.jobnest.services.UsersService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProfileController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsersService usersService;

    @MockBean
    private ProfileService profileService;

    @MockBean
    private AuthenticationService authenticationService;

    @Test
    @WithMockUser(username = "test@example.com")
    void testGetProfile_Success() throws Exception {
        Users mockUser = new Users();
        mockUser.setEmail("test@example.com");
        mockUser.setUserId(1);
        UsersType type = new UsersType();
        type.setUserTypeId(2);
        mockUser.setUserTypeId(type);

        when(authenticationService.getCurrentAuthenticatedUser()).thenReturn(mockUser);
        JobSeekerProfile mockProfile = new JobSeekerProfile();
        mockProfile.setFirstName("John");
        mockProfile.setLastName("Doe");
        when(profileService.getJobSeekerProfile(anyInt())).thenReturn(mockProfile);

        mockMvc.perform(get("/profile/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile-edit"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void testGetProfile_Unauthorized() throws Exception {
        // No mock authenticaton - should redirect or fail depending on security config
        // Actually the controller manually checks:
        when(authenticationService.getCurrentAuthenticatedUser()).thenReturn(null);

        mockMvc.perform(get("/profile/edit"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser
    void testGetProfile_RecruiterRedirectsDashboard() throws Exception {
        Users mockUser = new Users();
        mockUser.setUserId(2);
        UsersType type = new UsersType();
        type.setUserTypeId(1);
        mockUser.setUserTypeId(type);

        when(authenticationService.getCurrentAuthenticatedUser()).thenReturn(mockUser);

        mockMvc.perform(get("/profile/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));
    }

    @Test
    @WithMockUser
    void testGetProfile_UserTypeMissingRedirectsLogin() throws Exception {
        Users mockUser = new Users();
        mockUser.setUserId(3);
        mockUser.setUserTypeId(null);

        when(authenticationService.getCurrentAuthenticatedUser()).thenReturn(mockUser);

        mockMvc.perform(get("/profile/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error=true"));
    }

    @Test
    void testUpdateProfile_Unauthorized() throws Exception {
        when(authenticationService.getCurrentAuthenticatedUser()).thenReturn(null);

        mockMvc.perform(post("/profile/update")
                        .param("firstName", "Jane")
                        .param("lastName", "Doe"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error=true"));
    }

    @Test
    @WithMockUser
    void testUpdateProfile_JobSeekerSuccess() throws Exception {
        Users mockUser = new Users();
        mockUser.setUserId(4);
        UsersType type = new UsersType();
        type.setUserTypeId(2);
        mockUser.setUserTypeId(type);

        when(authenticationService.getCurrentAuthenticatedUser()).thenReturn(mockUser);
        when(profileService.updateJobSeekerProfile(any(), anyInt()))
                .thenReturn(new JobSeekerProfile());

        mockMvc.perform(post("/profile/update")
                        .param("firstName", "Jane")
                        .param("lastName", "Doe")
                        .param("city", "Baku"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard?success=true"));
    }

    @Test
    @WithMockUser
    void testUpdateProfile_JobSeekerErrorReturnsForm() throws Exception {
        Users mockUser = new Users();
        mockUser.setUserId(5);
        UsersType type = new UsersType();
        type.setUserTypeId(2);
        mockUser.setUserTypeId(type);

        when(authenticationService.getCurrentAuthenticatedUser()).thenReturn(mockUser);
        when(profileService.updateJobSeekerProfile(any(), anyInt()))
                .thenThrow(new RuntimeException("Update failed"));
        JobSeekerProfile mockProfile = new JobSeekerProfile();
        mockProfile.setFirstName("John");
        when(profileService.getJobSeekerProfile(5)).thenReturn(mockProfile);

        mockMvc.perform(post("/profile/update")
                        .param("firstName", "Jane")
                        .param("lastName", "Doe"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile-edit"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @WithMockUser
    void testUpdateProfile_RecruiterSuccess() throws Exception {
        Users mockUser = new Users();
        mockUser.setUserId(6);
        UsersType type = new UsersType();
        type.setUserTypeId(1);
        mockUser.setUserTypeId(type);

        when(authenticationService.getCurrentAuthenticatedUser()).thenReturn(mockUser);
        when(profileService.updateRecruiterProfile(any(), anyInt()))
                .thenReturn(new RecruiterProfile());

        mockMvc.perform(post("/profile/update")
                        .param("company", "ACME"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard?success=true"));
    }
}
