package com.example.jobnest.controller;

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

        private final AdminReportService adminReportService;

        @GetMapping("/login")
        public String adminLogin() {
                return "admin-login";
        }

        @GetMapping("/dashboard")
        public String dashboard(Model model) {
                model.addAttribute("dashboardData", adminReportService.getDashboardStats());
                return "admin-dashboard";
        }

        @GetMapping("/reports")
        public String reports(Model model) {
                model.addAttribute("reportsData", adminReportService.getDetailedReports());
                return "admin-reports";
        }
}
