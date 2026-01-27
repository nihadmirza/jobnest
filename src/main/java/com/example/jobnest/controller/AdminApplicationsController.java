package com.example.jobnest.controller;

import com.example.jobnest.services.JobSeekerApplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminApplicationsController {

    private final JobSeekerApplyService jobSeekerApplyService;

    @GetMapping("/applications")
    public String listAllApplications(Model model) {
        model.addAttribute("applications", jobSeekerApplyService.getAllApplications());
        return "admin-applications";
    }
}
