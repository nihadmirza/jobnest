package com.example.jobnest.controller;

import com.example.jobnest.entity.Users;
import com.example.jobnest.entity.UsersType;
import com.example.jobnest.entity.JobSeekerProfile;
import com.example.jobnest.dto.response.ProfileEditPageDTO;
import com.example.jobnest.services.ProfilePageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProfileController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProfilePageService profilePageService;

    @Test
    @WithMockUser(username = "test@example.com")
    void testGetProfile_Success() throws Exception {
        Users mockUser = new Users();
        mockUser.setEmail("test@example.com");
        mockUser.setUserId(1);
        UsersType type = new UsersType();
        type.setUserTypeId(2);
        mockUser.setUserTypeId(type);
        JobSeekerProfile mockProfile = new JobSeekerProfile();
        mockProfile.setFirstName("John");
        mockProfile.setLastName("Doe");

        when(profilePageService.editProfilePage()).thenReturn(
                new ProfilePageService.PageResult("profile-edit", java.util.Map.of(
                        "page", ProfileEditPageDTO.builder()
                                .user(mockUser)
                                .profile(mockProfile)
                                .build()
                ))
        );

        mockMvc.perform(get("/profile/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile-edit"))
                .andExpect(model().attributeExists("page"));
    }

    @Test
    void testGetProfile_Unauthorized() throws Exception {
        when(profilePageService.editProfilePage()).thenReturn(
                new ProfilePageService.PageResult("redirect:/login?error=true", java.util.Map.of())
        );

        mockMvc.perform(get("/profile/edit"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser
    void testGetProfile_RecruiterRedirectsDashboard() throws Exception {
        when(profilePageService.editProfilePage()).thenReturn(
                new ProfilePageService.PageResult("redirect:/dashboard", java.util.Map.of())
        );

        mockMvc.perform(get("/profile/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));
    }

    @Test
    @WithMockUser
    void testGetProfile_UserTypeMissingRedirectsLogin() throws Exception {
        when(profilePageService.editProfilePage()).thenReturn(
                new ProfilePageService.PageResult("redirect:/login?error=true", java.util.Map.of("error", "İstifadəçi tipi tapılmadı"))
        );

        mockMvc.perform(get("/profile/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error=true"));
    }

    @Test
    void testUpdateProfile_Unauthorized() throws Exception {
        when(profilePageService.updateProfile(any())).thenReturn(
                new ProfilePageService.PageResult("redirect:/login?error=true", java.util.Map.of())
        );

        mockMvc.perform(post("/profile/update")
                        .param("firstName", "Jane")
                        .param("lastName", "Doe"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error=true"));
    }

    @Test
    @WithMockUser
    void testUpdateProfile_JobSeekerSuccess() throws Exception {
        when(profilePageService.updateProfile(any())).thenReturn(
                new ProfilePageService.PageResult("redirect:/dashboard?success=true", java.util.Map.of())
        );

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
        JobSeekerProfile mockProfile = new JobSeekerProfile();
        mockProfile.setFirstName("John");

        when(profilePageService.updateProfile(any())).thenReturn(
                new ProfilePageService.PageResult("profile-edit", java.util.Map.of(
                        "page", ProfileEditPageDTO.builder()
                                .user(mockUser)
                                .profile(mockProfile)
                                .error("Update failed")
                                .build()
                ))
        );

        mockMvc.perform(post("/profile/update")
                        .param("firstName", "Jane")
                        .param("lastName", "Doe"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile-edit"))
                .andExpect(model().attributeExists("page"));
    }

    @Test
    @WithMockUser
    void testUpdateProfile_RecruiterSuccess() throws Exception {
        when(profilePageService.updateProfile(any())).thenReturn(
                new ProfilePageService.PageResult("redirect:/dashboard?success=true", java.util.Map.of())
        );

        mockMvc.perform(post("/profile/update")
                        .param("company", "ACME"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard?success=true"));
    }
}
