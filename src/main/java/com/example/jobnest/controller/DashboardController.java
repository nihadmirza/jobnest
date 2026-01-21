package com.example.jobnest.controller;

import com.example.jobnest.dto.response.JobSeekerDashboardDTO;
import com.example.jobnest.dto.response.RecruiterDashboardDTO;
import com.example.jobnest.entity.Users;
import com.example.jobnest.services.AuthenticationService;
import com.example.jobnest.services.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for dashboard page.
 * Follows clean architecture - all data aggregation delegated to
 * DashboardService.
 */
@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final AuthenticationService authenticationService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Users user = authenticationService.getCurrentAuthenticatedUser();
        if (user == null || user.getUserTypeId() == null) {
            return "redirect:/login?error=true";
        }

        int userTypeId = user.getUserTypeId().getUserTypeId();

        if (userTypeId == 1) {
            // Recruiter dashboard
            RecruiterDashboardDTO data = dashboardService.getRecruiterDashboardData(user.getUserId());
            model.addAttribute("profile", data.getProfile());
            model.addAttribute("user", data.getProfile());
            model.addAttribute("userType", "RECRUITER");
            model.addAttribute("applicationCount", data.getApplicationCount());
            model.addAttribute("pendingApplicationCount", data.getPendingApplicationCount());
        } else {
            // Job Seeker dashboard
            JobSeekerDashboardDTO data = dashboardService.getJobSeekerDashboardData(user.getUserId());
            model.addAttribute("profile", data.getProfile());
            model.addAttribute("user", data.getProfile());
            model.addAttribute("userType", "JOB_SEEKER");
            model.addAttribute("applications", data.getApplications());
        }

        return "dashboard";
    }
}
