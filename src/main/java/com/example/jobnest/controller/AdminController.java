package com.example.jobnest.controller;

import com.example.jobnest.dto.response.AdminDashboardDTO;
import com.example.jobnest.dto.response.AdminReportsDTO;
import com.example.jobnest.services.AdminReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for admin functionality.
 * Follows clean architecture - all analytics delegated to AdminReportService.
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

        private static final String ERROR_ATTR = "error";
        private static final String ERROR_VIEW = "error";

        private final AdminReportService adminReportService;

        @GetMapping("/login")
        public String adminLogin(@org.springframework.web.bind.annotation.RequestParam(required = false) String error,
                        @org.springframework.web.bind.annotation.RequestParam(required = false) String blocked,
                        Model model) {
                if (blocked != null) {
                        model.addAttribute(ERROR_ATTR, "Too many failed attempts. Account blocked for 15 minutes.");
                } else if (error != null) {
                        model.addAttribute(ERROR_ATTR, "Invalid credentials");
                }
                return "admin-login";
        }

        @GetMapping("/dashboard")
        public String dashboard(Model model) {
                try {
                        AdminDashboardDTO data = adminReportService.getDashboardStats();

                        model.addAttribute("totalUsers", data.getTotalUsers());
                        model.addAttribute("activeJobs", data.getActiveJobs());
                        model.addAttribute("totalApplications", data.getTotalApplications());

                        return "admin-dashboard";
                } catch (Exception e) {
                        model.addAttribute(ERROR_ATTR, "Error loading dashboard: " + e.getMessage());
                        return ERROR_VIEW;
                }
        }

        @GetMapping("/reports")
        public String reports(Model model) {
                try {
                        AdminReportsDTO data = adminReportService.getDetailedReports();

                        // Add all report data to model
                        model.addAttribute("totalUsers", data.getTotalUsers());
                        model.addAttribute("recruiters", data.getRecruiters());
                        model.addAttribute("jobSeekers", data.getJobSeekers());
                        model.addAttribute("totalJobs", data.getTotalJobs());
                        model.addAttribute("activeJobs", data.getActiveJobs());
                        model.addAttribute("totalApplications", data.getTotalApplications());
                        model.addAttribute("avgApplications", data.getAvgApplications());
                        model.addAttribute("topJobs", data.getTopJobs());
                        model.addAttribute("topRecruiters", data.getTopRecruiters());
                        model.addAttribute("topJobSeekers", data.getTopJobSeekers());

                        return "admin-reports";
                } catch (Exception e) {
                        model.addAttribute(ERROR_ATTR, "Error loading reports: " + e.getMessage());
                        return ERROR_VIEW;
                }
        }
}
