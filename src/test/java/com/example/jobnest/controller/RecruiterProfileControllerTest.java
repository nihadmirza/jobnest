package com.example.jobnest.controller;

import com.example.jobnest.dto.request.ProfileUpdateRequest;
import com.example.jobnest.entity.RecruiterProfile;
import com.example.jobnest.entity.Users;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class RecruiterProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProfileService profileService;

    @MockBean
    private AuthenticationService authenticationService;

    private Users user;
    private RecruiterProfile profile;

    @BeforeEach
    void setUp() {
        user = new Users();
        user.setUserId(1);
        user.setEmail("recruiter@test.com");

        profile = new RecruiterProfile(user);
        profile.setUserAccountId(100);
        profile.setFirstName("John");
    }

    @Test
    @WithMockUser
    void editProfile_ShouldReturnEditView() throws Exception {
        when(authenticationService.getCurrentAuthenticatedUser()).thenReturn(user);
        when(profileService.getRecruiterProfile(1)).thenReturn(profile);

        mockMvc.perform(get("/recruiter/profile/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("recruiter-profile-edit"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @WithMockUser
    void updateProfile_ShouldRedirectToDashboard_OnSuccess() throws Exception {
        when(authenticationService.getCurrentAuthenticatedUser()).thenReturn(user);
        when(profileService.updateRecruiterProfile(any(ProfileUpdateRequest.class), anyInt())).thenReturn(profile);

        mockMvc.perform(post("/recruiter/profile/update")
                .param("firstName", "Jane")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard?success=true"));
    }

    @Test
    @WithMockUser
    void updateProfile_ShouldReturnEditView_OnException() throws Exception {
        when(authenticationService.getCurrentAuthenticatedUser()).thenReturn(user);
        doThrow(new RuntimeException("Update failed")).when(profileService)
                .updateRecruiterProfile(any(ProfileUpdateRequest.class), anyInt());
        when(profileService.getRecruiterProfile(1)).thenReturn(profile);

        mockMvc.perform(post("/recruiter/profile/update")
                .param("firstName", "Jane")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("recruiter-profile-edit"))
                .andExpect(model().attributeExists("error"));
    }
}
