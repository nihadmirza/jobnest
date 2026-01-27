package com.example.jobnest.controller;

import com.example.jobnest.dto.request.ProfileUpdateRequest;
import com.example.jobnest.dto.response.RecruiterProfileEditPageDTO;
import com.example.jobnest.entity.RecruiterProfile;
import com.example.jobnest.entity.Users;
import com.example.jobnest.services.RecruiterProfilePageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecruiterProfileController.class)
@AutoConfigureMockMvc(addFilters = false)
@SuppressWarnings("null")
class RecruiterProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecruiterProfilePageService recruiterProfilePageService;

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
        when(recruiterProfilePageService.editProfilePage()).thenReturn(
                new RecruiterProfilePageService.PageResult("recruiter-profile-edit",
                        java.util.Map.of("page", RecruiterProfileEditPageDTO.builder()
                                .user(user)
                                .profile(profile)
                                .build()))
        );

        mockMvc.perform(get("/recruiter/profile/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("recruiter-profile-edit"))
                .andExpect(model().attributeExists("page"));
    }

    @Test
    @WithMockUser
    void updateProfile_ShouldRedirectToDashboard_OnSuccess() throws Exception {
        when(recruiterProfilePageService.updateProfile(any(ProfileUpdateRequest.class))).thenReturn(
                new RecruiterProfilePageService.PageResult("redirect:/dashboard?success=true", java.util.Map.of())
        );

        mockMvc.perform(post("/recruiter/profile/update")
                .param("firstName", "Jane")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard?success=true"));
    }

    @Test
    @WithMockUser
    void updateProfile_ShouldReturnEditView_OnException() throws Exception {
        when(recruiterProfilePageService.updateProfile(any(ProfileUpdateRequest.class))).thenReturn(
                new RecruiterProfilePageService.PageResult("recruiter-profile-edit",
                        java.util.Map.of("page", RecruiterProfileEditPageDTO.builder()
                                .user(user)
                                .profile(profile)
                                .error("Update failed")
                                .build()))
        );

        mockMvc.perform(post("/recruiter/profile/update")
                .param("firstName", "Jane")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("recruiter-profile-edit"))
                .andExpect(model().attributeExists("page"));
    }
}
