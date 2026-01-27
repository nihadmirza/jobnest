package com.example.jobnest.controller;

import com.example.jobnest.dto.request.UserRegistrationRequest;
import com.example.jobnest.dto.response.RegisterPageDTO;
import com.example.jobnest.services.RegistrationPageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(UsersController.class)
@AutoConfigureMockMvc(addFilters = false)
@SuppressWarnings("null")
class UsersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RegistrationPageService registrationPageService;

    @Test
    void register_rendersViewWithDefaults() throws Exception {
        RegisterPageDTO page = RegisterPageDTO.builder()
                .request(new UserRegistrationRequest())
                .build();
        page.getRequest().setRole("CANDIDATE");
        when(registrationPageService.showRegisterPage("CANDIDATE"))
                .thenReturn(new RegistrationPageService.PageResult("register", Map.of("page", page)));

        mockMvc.perform(get("/register").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("page"));
    }

    @Test
    void userRegistration_returnsViewOnValidationError() throws Exception {
        when(registrationPageService.showRegisterPage(any(RegisterPageDTO.class)))
                .thenReturn(new RegistrationPageService.PageResult("register", Map.of("page", RegisterPageDTO.builder()
                        .request(new UserRegistrationRequest())
                        .build())));

        mockMvc.perform(post("/register/new").with(csrf())
                        .param("request.email", "bad-email")
                        .param("request.role", "CANDIDATE"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("page"));
    }

    @Test
    void userRegistration_redirectsOnSuccess() throws Exception {
        when(registrationPageService.registerNewUser(any(RegisterPageDTO.class), any()))
                .thenReturn(new RegistrationPageService.PageResult("redirect:/dashboard", Map.of()));

        mockMvc.perform(post("/register/new").with(csrf())
                        .param("request.firstName", "John")
                        .param("request.lastName", "Doe")
                        .param("request.company", "ACME")
                        .param("request.email", "john@example.com")
                        .param("request.password", "secret1")
                        .param("request.role", "CANDIDATE"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));
    }

    @Test
    void userRegistration_returnsViewOnServiceError() throws Exception {
        when(registrationPageService.registerNewUser(any(RegisterPageDTO.class), any()))
                .thenReturn(new RegistrationPageService.PageResult("register", Map.of("page", RegisterPageDTO.builder()
                        .request(new UserRegistrationRequest())
                        .error("Failed")
                        .build())));

        mockMvc.perform(post("/register/new").with(csrf())
                        .param("request.firstName", "John")
                        .param("request.lastName", "Doe")
                        .param("request.company", "ACME")
                        .param("request.email", "john@example.com")
                        .param("request.password", "secret1")
                        .param("request.role", "CANDIDATE"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("page"));
    }
}
