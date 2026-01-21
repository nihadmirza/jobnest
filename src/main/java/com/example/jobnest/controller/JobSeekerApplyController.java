package com.example.jobnest.controller;

import com.example.jobnest.dto.response.RecruiterApplicationStatsDTO;
import com.example.jobnest.entity.JobSeekerApply;
import com.example.jobnest.entity.JobSeekerProfile;
import com.example.jobnest.entity.RecruiterProfile;
import com.example.jobnest.entity.Users;
import com.example.jobnest.services.ApplicationManagementService;
import com.example.jobnest.services.AuthenticationService;
import com.example.jobnest.services.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for job seeker application management.
 * Follows clean architecture - all business logic delegated to
 * ApplicationManagementService.
 */
@Controller
@RequiredArgsConstructor
public class JobSeekerApplyController {

    private final ApplicationManagementService applicationManagementService;
    private final AuthenticationService authenticationService;
    private final ProfileService profileService;

    @GetMapping("/recruiter/applications")
    public String viewApplications(
            @RequestParam(required = false) Integer jobId,
            @RequestParam(required = false) String status,
            Model model) {

        Users user = authenticationService.getCurrentAuthenticatedUser();
        RecruiterProfile recruiterProfile = profileService.getRecruiterProfile(user.getUserId());
        int recruiterId = recruiterProfile.getUserAccountId();

        // Get applications with statistics via service
        RecruiterApplicationStatsDTO data = applicationManagementService
                .getApplicationsWithStats(recruiterId, jobId, status);

        // Add to model
        model.addAttribute("applications", data.getApplications());
        model.addAttribute("candidateProfilesMap", data.getCandidateProfilesMap());
        model.addAttribute("allJobs", data.getAllJobs());
        model.addAttribute("selectedJobId", jobId);
        model.addAttribute("selectedStatus", status != null ? status : "All");
        model.addAttribute("totalApplications", data.getTotalApplications());
        model.addAttribute("pendingCount", data.getPendingCount());
        model.addAttribute("reviewedCount", data.getReviewedCount());
        model.addAttribute("acceptedCount", data.getAcceptedCount());
        model.addAttribute("rejectedCount", data.getRejectedCount());

        return "recruiter-applications";
    }

    @PostMapping("/recruiter/applications/{applyId}/status")
    public String updateApplicationStatus(
            @PathVariable int applyId,
            @RequestParam String status,
            @RequestParam(required = false) Integer jobId,
            @RequestParam(required = false) String filterStatus,
            RedirectAttributes redirectAttributes) {

        Users user = authenticationService.getCurrentAuthenticatedUser();
        RecruiterProfile recruiterProfile = profileService.getRecruiterProfile(user.getUserId());

        try {
            // Service handles authorization check
            applicationManagementService.updateApplicationStatus(
                    applyId, status, recruiterProfile.getUserAccountId());
            redirectAttributes.addFlashAttribute("success", "Application status updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        // Preserve filters
        StringBuilder redirectUrl = new StringBuilder("/recruiter/applications");
        if (jobId != null && jobId > 0) {
            redirectUrl.append("?jobId=").append(jobId);
        }
        if (filterStatus != null && !filterStatus.isEmpty() && !filterStatus.equals("All")) {
            redirectUrl.append(redirectUrl.toString().contains("?") ? "&" : "?");
            redirectUrl.append("status=").append(filterStatus);
        }

        return "redirect:" + redirectUrl.toString();
    }

    @GetMapping("/recruiter/applications/{applyId}/candidate-profile")
    public String viewCandidateProfile(
            @PathVariable int applyId,
            Model model,
            RedirectAttributes redirectAttributes) {

        Users user = authenticationService.getCurrentAuthenticatedUser();
        RecruiterProfile recruiterProfile = profileService.getRecruiterProfile(user.getUserId());

        try {
            // Service handles authorization check
            JobSeekerApply application = applicationManagementService
                    .getApplicationForRecruiter(applyId, recruiterProfile.getUserAccountId());

            JobSeekerProfile candidateProfile = profileService
                    .getJobSeekerProfile(application.getUser().getUserId());

            model.addAttribute("application", application);
            model.addAttribute("candidateProfile", candidateProfile);
            model.addAttribute("candidateUser", application.getUser());
            model.addAttribute("job", application.getJob());

            return "candidate-profile-view";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/recruiter/applications";
        }
    }
}
