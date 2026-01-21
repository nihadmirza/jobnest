package com.example.jobnest.controller;

import com.example.jobnest.entity.Job;
import com.example.jobnest.entity.RecruiterProfile;
import com.example.jobnest.entity.Users;
import com.example.jobnest.services.AuthenticationService;
import com.example.jobnest.services.JobSeekerApplyService;
import com.example.jobnest.services.JobService;
import com.example.jobnest.services.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

/**
 * Controller for job operations.
 * Follows clean architecture - uses services for business logic.
 */
@Controller
@RequiredArgsConstructor
public class JobController {

    private static final String PROFILE_ATTR = "profile";
    private static final String JOB_FORM_VIEW = "job-form";
    private static final String REDIRECT_RECRUITER_JOBS = "redirect:/recruiter/jobs";
    private static final String ERROR_ATTR = "error";

    private final JobService jobService;
    private final JobSeekerApplyService jobSeekerApplyService;
    private final AuthenticationService authenticationService;
    private final ProfileService profileService;

    @GetMapping("/global-search")
    public String globalSearch(@RequestParam(required = false) String job,
            @RequestParam(required = false) String location,
            Model model) {
        List<Job> jobs = jobService.searchJobs(job, location, null);
        model.addAttribute("jobs", jobs);
        model.addAttribute("job", job);
        model.addAttribute("location", location);
        return "jobs-list"; // Redirecting to uniform jobs-list view as requested
    }

    @GetMapping("/jobs")
    public String listAllJobs(@RequestParam(required = false) String job,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String type,
            Model model) {
        List<Job> jobs;

        // If any filter is present, use search
        if ((job != null && !job.isEmpty()) || (location != null && !location.isEmpty())
                || (type != null && !type.isEmpty())) {
            jobs = jobService.searchJobs(job, location, type);
        } else {
            jobs = jobService.findAllActiveJobs();
        }

        model.addAttribute("jobs", jobs);
        // Add current selection to model for filters
        model.addAttribute("keyword", job); // 'job' param maps to 'keyword' in template usually
        model.addAttribute("location", location);
        model.addAttribute("type", type);

        return "jobs-list";
    }

    @GetMapping("/jobs/{id}")
    public String viewJob(@PathVariable int id, Model model) {
        Optional<Job> job = jobService.findById(id);
        if (job.isPresent() && job.get().isActive()) {
            model.addAttribute("job", job.get());

            // Check authentication using AuthenticationService
            Users user = authenticationService.getCurrentAuthenticatedUser();
            boolean isAuthenticated = (user != null);
            boolean isCandidate = false;
            boolean hasApplied = false;

            if (isAuthenticated && user.getUserTypeId() != null) {
                int userTypeId = user.getUserTypeId().getUserTypeId();
                isCandidate = (userTypeId != 1); // 1 = RECRUITER

                if (isCandidate) {
                    // Check if user already applied
                    hasApplied = jobSeekerApplyService.hasUserApplied(id, user.getUserId());
                }
            }

            model.addAttribute("isCandidate", isCandidate);
            model.addAttribute("hasApplied", hasApplied);
            model.addAttribute("isAuthenticated", isAuthenticated);

            return "job-details";
        }
        return "redirect:/jobs";
    }

    @GetMapping("/recruiter/jobs")
    public String listRecruiterJobs(Model model) {
        Users user = authenticationService.getCurrentAuthenticatedUser();
        RecruiterProfile recruiterProfile = profileService.getRecruiterProfile(user.getUserId());

        List<Job> jobs = jobService.findJobsByRecruiterId(recruiterProfile.getUserAccountId());
        model.addAttribute("jobs", jobs);
        model.addAttribute(PROFILE_ATTR, recruiterProfile);
        return "recruiter-jobs-list";
    }

    @GetMapping("/recruiter/jobs/new")
    public String showCreateJobForm(Model model) {
        Users user = authenticationService.getCurrentAuthenticatedUser();
        RecruiterProfile profile = profileService.getRecruiterProfile(user.getUserId());
        model.addAttribute(PROFILE_ATTR, profile);
        model.addAttribute("job", new Job());
        return JOB_FORM_VIEW;
    }

    @PostMapping("/recruiter/jobs")
    public String createJob(@Valid @ModelAttribute Job job, BindingResult bindingResult, Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("job", job);
            return JOB_FORM_VIEW;
        }

        Users user = authenticationService.getCurrentAuthenticatedUser();
        RecruiterProfile recruiterProfile = profileService.getRecruiterProfile(user.getUserId());

        // Set job as DRAFT with PENDING payment status
        job.setRecruiter(recruiterProfile);
        job.setPaymentStatus("PENDING");
        job.setActive(false);

        Job savedJob = jobService.save(job);

        // Redirect to pricing page to select plan and pay
        return "redirect:/recruiter/pricing?jobId=" + savedJob.getJobId();
    }

    @GetMapping("/recruiter/jobs/{id}/edit")
    public String showEditJobForm(@PathVariable int id, Model model) {
        Users user = authenticationService.getCurrentAuthenticatedUser();
        RecruiterProfile profile = profileService.getRecruiterProfile(user.getUserId());
        model.addAttribute(PROFILE_ATTR, profile);

        Optional<Job> job = jobService.findById(id);
        if (job.isPresent()) {
            model.addAttribute("job", job.get());
            return JOB_FORM_VIEW;
        }
        return REDIRECT_RECRUITER_JOBS;
    }

    @PostMapping("/recruiter/jobs/{id}")
    public String updateJob(@PathVariable int id, @Valid @ModelAttribute Job job, BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("job", job);
            return JOB_FORM_VIEW;
        }

        Optional<Job> existingJob = jobService.findById(id);
        if (existingJob.isEmpty()) {
            return REDIRECT_RECRUITER_JOBS;
        }

        Job jobToUpdate = existingJob.get();
        jobToUpdate.setTitle(job.getTitle());
        jobToUpdate.setDescription(job.getDescription());
        jobToUpdate.setLocation(job.getLocation());
        jobToUpdate.setCity(job.getCity());
        jobToUpdate.setState(job.getState());
        jobToUpdate.setCountry(job.getCountry());
        jobToUpdate.setEmploymentType(job.getEmploymentType());
        jobToUpdate.setSalary(job.getSalary());
        jobToUpdate.setActive(job.isActive());

        jobService.update(jobToUpdate);
        return REDIRECT_RECRUITER_JOBS;
    }

    @PostMapping("/recruiter/jobs/{id}/delete")
    public String deleteJob(@PathVariable int id) {
        jobService.deleteById(id);
        return REDIRECT_RECRUITER_JOBS;
    }

    @PostMapping("/recruiter/jobs/{id}/archive")
    public String archiveJob(@PathVariable int id) {
        jobService.archive(id);
        return REDIRECT_RECRUITER_JOBS;
    }

    @PostMapping("/jobs/{id}/apply")
    public String applyToJob(@PathVariable int id,
            @RequestParam(required = false) String coverLetter,
            RedirectAttributes redirectAttributes) {
        // Check authentication using service
        Users user = authenticationService.getCurrentAuthenticatedUser();
        if (user == null) {
            redirectAttributes.addFlashAttribute(ERROR_ATTR, "Please login to apply for jobs");
            return "redirect:/login";
        }

        // Check if user is a Job Seeker
        if (!authenticationService.hasRole("JOB SEEKER")) {
            redirectAttributes.addFlashAttribute(ERROR_ATTR, "Only Job Seekers can apply for jobs.");
            if (authenticationService.hasRole("RECRUITER")) {
                return "redirect:/dashboard";
            }
            return "redirect:/login";
        }

        try {
            jobSeekerApplyService.applyToJob(id, user.getUserId(), coverLetter);
            redirectAttributes.addFlashAttribute("success", "Application submitted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR_ATTR, e.getMessage());
        }

        return "redirect:/jobs/" + id;
    }
}
