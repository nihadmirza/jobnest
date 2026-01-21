package com.example.jobnest.controller;

import com.example.jobnest.entity.JobSeekerApply;
import com.example.jobnest.services.JobSeekerApplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminApplicationsController {

    private final JobSeekerApplyService jobSeekerApplyService;

    @GetMapping("/applications")
    public String listAllApplications(Model model) {
        try {
            List<JobSeekerApply> applications = jobSeekerApplyService.getAllApplications();
            model.addAttribute("applications", applications);
            return "admin-applications";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading applications: " + e.getMessage());
            return "error";
        }
    }
}
