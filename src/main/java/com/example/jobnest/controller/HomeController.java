package com.example.jobnest.controller;

import com.example.jobnest.dto.response.HomePageDataDTO;
import com.example.jobnest.services.HomePageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for homepage.
 * Follows clean architecture - all data aggregation delegated to
 * HomePageService.
 */
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final HomePageService homePageService;

    @GetMapping("/")
    public String showHome(Model model) {
        HomePageDataDTO data = homePageService.getHomePageData();

        model.addAttribute("featuredJobs", data.getFeaturedJobs());
        model.addAttribute("totalUsers", data.getTotalJobs());
        model.addAttribute("totalCompanies", data.getTotalRecruiters());

        return "index";
    }
}
