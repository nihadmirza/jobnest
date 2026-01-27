package com.example.jobnest.controller;

import com.example.jobnest.entity.Job;
import com.example.jobnest.services.JobPageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for job operations.
 * Follows clean architecture - uses services for business logic.
 */
@Controller
@RequiredArgsConstructor
@SuppressWarnings("null")
public class JobController {

    private static final String JOB_FORM_VIEW = "job-form";
    private static final String REDIRECT_RECRUITER_JOBS = "redirect:/recruiter/jobs";
    private final JobPageService jobPageService;

    @GetMapping("/global-search")
    public String globalSearch(@RequestParam(required = false) String job,
            @RequestParam(required = false) String location,
            Model model) {
        JobPageService.PageResult result = jobPageService.globalSearch(job, location);
        model.addAllAttributes(result.model());
        return result.viewName();
    }

    @GetMapping("/jobs")
    public String listAllJobs(@RequestParam(required = false) String job,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String type,
            Model model) {
        JobPageService.PageResult result = jobPageService.listAllJobs(job, location, type);
        model.addAllAttributes(result.model());
        return result.viewName();
    }

    @GetMapping("/jobs/{id}")
    public String viewJob(@PathVariable int id, Model model) {
        JobPageService.PageResult result = jobPageService.viewJob(id);
        model.addAllAttributes(result.model());
        return result.viewName();
    }

    @GetMapping("/recruiter/jobs")
    public String listRecruiterJobs(Model model) {
        JobPageService.PageResult result = jobPageService.listRecruiterJobs();
        model.addAllAttributes(result.model());
        return result.viewName();
    }

    @GetMapping("/recruiter/jobs/new")
    public String showCreateJobForm(Model model) {
        JobPageService.PageResult result = jobPageService.showCreateJobForm();
        model.addAllAttributes(result.model());
        return result.viewName();
    }

    @PostMapping("/recruiter/jobs")
    public String createJob(@Valid @ModelAttribute Job job, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            JobPageService.PageResult result = jobPageService.showCreateJobForm(job);
            model.addAllAttributes(result.model());
            return result.viewName();
        }
        int jobId = jobPageService.createRecruiterJobDraft(job);
        return "redirect:/recruiter/pricing?jobId=" + jobId;
    }

    @GetMapping("/recruiter/jobs/{id}/edit")
    public String showEditJobForm(@PathVariable int id, Model model) {
        JobPageService.PageResult result = jobPageService.showEditJobForm(id);
        model.addAllAttributes(result.model());
        return result.viewName();
    }

    @PostMapping("/recruiter/jobs/{id}")
    public String updateJob(@PathVariable int id, @Valid @ModelAttribute Job job, BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("job", job);
            return JOB_FORM_VIEW;
        }
        jobPageService.updateJob(id, job);
        return REDIRECT_RECRUITER_JOBS;
    }

    @PostMapping("/recruiter/jobs/{id}/delete")
    public String deleteJob(@PathVariable int id) {
        jobPageService.deleteJob(id);
        return REDIRECT_RECRUITER_JOBS;
    }

    @PostMapping("/recruiter/jobs/{id}/archive")
    public String archiveJob(@PathVariable int id) {
        jobPageService.archiveJob(id);
        return REDIRECT_RECRUITER_JOBS;
    }

    @PostMapping("/jobs/{id}/apply")
    public String applyToJob(@PathVariable int id,
            @RequestParam(required = false) String coverLetter,
            RedirectAttributes redirectAttributes) {
        JobPageService.ApplyResult result = jobPageService.applyToJob(id, coverLetter);
        if (result.flashKey() != null) redirectAttributes.addFlashAttribute(result.flashKey(), result.flashMessage());
        return result.redirectTo();
    }
}
