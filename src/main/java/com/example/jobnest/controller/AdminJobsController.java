package com.example.jobnest.controller;

import com.example.jobnest.entity.Job;
import com.example.jobnest.services.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminJobsController {

    private final JobService jobService;

    @GetMapping("/jobs")
    public String listAllJobs(Model model) {
        try {
            List<Job> jobs = jobService.findAllJobs();
            model.addAttribute("jobs", jobs);
            return "admin-jobs";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading jobs: " + e.getMessage());
            return "error";
        }
    }
}
