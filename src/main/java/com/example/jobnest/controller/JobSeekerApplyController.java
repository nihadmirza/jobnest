package com.example.jobnest.controller;

import com.example.jobnest.services.RecruiterApplicationsPageService;
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
@SuppressWarnings("null")
public class JobSeekerApplyController {

    private final RecruiterApplicationsPageService recruiterApplicationsPageService;

    @GetMapping("/recruiter/applications")
    public String viewApplications(
            @RequestParam(required = false) Integer jobId,
            @RequestParam(required = false) String status,
            Model model) {
        RecruiterApplicationsPageService.PageResult result = recruiterApplicationsPageService.viewApplications(jobId,
                status);
        model.addAllAttributes(result.model());
        return result.viewName();
    }

    @PostMapping("/recruiter/applications/{applyId}/status")
    public String updateApplicationStatus(
            @PathVariable int applyId,
            @RequestParam String status,
            @RequestParam(required = false) Integer jobId,
            @RequestParam(required = false) String filterStatus,
            RedirectAttributes redirectAttributes) {
        RecruiterApplicationsPageService.ActionResult result = recruiterApplicationsPageService.updateApplicationStatus(
                applyId, status, jobId, filterStatus);
        if (result.flashKey() != null) {
            redirectAttributes.addFlashAttribute(result.flashKey(), result.flashMessage());
        }
        return result.redirectTo();
    }

    @GetMapping("/recruiter/applications/{applyId}/candidate-profile")
    public String viewCandidateProfile(
            @PathVariable int applyId,
            Model model,
            RedirectAttributes redirectAttributes) {
        RecruiterApplicationsPageService.PageResult result = recruiterApplicationsPageService.viewCandidateProfile(
                applyId);
        model.addAllAttributes(result.model());
        return result.viewName();
    }
}
