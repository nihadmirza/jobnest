package com.example.jobnest.controller;

import com.example.jobnest.entity.Users;
import com.example.jobnest.entity.UsersType;
import com.example.jobnest.services.UsersService;
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

@WebMvcTest(AdminUsersController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser
class AdminUsersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsersService usersService;

    @Test
    void listAllUsers_returnsViewOnSuccess() throws Exception {
        UsersType type = new UsersType();
        type.setUserTypeName("ADMIN");
        Users user = new Users();
        user.setUserId(1);
        user.setEmail("admin@example.com");
        user.setActive(true);
        user.setRegistrationDate(new Date());
        user.setUserTypeId(type);
        List<Users> users = List.of(user);
        when(usersService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/admin/users").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-users"))
                .andExpect(model().attribute("users", users));
    }

    @Test
    void listAllUsers_returnsErrorViewOnException() throws Exception {
        when(usersService.getAllUsers()).thenThrow(new RuntimeException("DB down"));

        mockMvc.perform(get("/admin/users").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("error"));
    }
}
