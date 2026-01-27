package com.example.jobnest.services;

import java.util.Map;

/**
 * Facade service for recruiter application pages.
 * Keeps controller thin by returning view + a single page DTO.
 */
public interface RecruiterApplicationsPageService {

    record PageResult(String viewName, Map<String, Object> model) {}

    record ActionResult(String redirectTo, String flashKey, String flashMessage) {}

    PageResult viewApplications(Integer jobId, String status);

    ActionResult updateApplicationStatus(int applyId, String status, Integer jobId, String filterStatus);

    PageResult viewCandidateProfile(int applyId);
}

