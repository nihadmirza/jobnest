package com.example.jobnest.controller;

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

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("dashboardData", dashboardService.getDashboardData());
        return "dashboard";
    }
}
