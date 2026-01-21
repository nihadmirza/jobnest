package com.example.jobnest.controller;

import com.example.jobnest.dto.response.HomePageDataDTO;
import com.example.jobnest.services.HomePageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HomePageService homePageService;

    private HomePageDataDTO homePageDataDTO;

    @BeforeEach
    void setUp() {
        homePageDataDTO = new HomePageDataDTO();
        homePageDataDTO.setFeaturedJobs(Collections.emptyList());
        homePageDataDTO.setTotalJobs(100L);
        homePageDataDTO.setTotalRecruiters(50L);
    }

    @Test
    @org.springframework.security.test.context.support.WithMockUser
    void showHome_ShouldReturnIndexView() throws Exception {
        when(homePageService.getHomePageData()).thenReturn(homePageDataDTO);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("featuredJobs"))
                .andExpect(model().attribute("totalUsers", 100L)) // Mapped to totalJobs in controller
                .andExpect(model().attribute("totalCompanies", 50L));
    }
}
