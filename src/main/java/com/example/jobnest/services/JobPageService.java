package com.example.jobnest.services;

import com.example.jobnest.entity.Job;

import java.util.Map;

/**
 * Facade service for JobController page/flow logic.
 * Keeps controllers thin by returning view + model data.
 */
public interface JobPageService {

    record PageResult(String viewName, Map<String, Object> model) {}

    record ApplyResult(String redirectTo, String flashKey, String flashMessage) {}

    PageResult globalSearch(String job, String location);

    PageResult listAllJobs(String job, String location, String type);

    PageResult viewJob(int jobId);

    PageResult listRecruiterJobs();

    PageResult showCreateJobForm();

    PageResult showCreateJobForm(Job jobWithErrors);

    int createRecruiterJobDraft(Job job);

    PageResult showEditJobForm(int jobId);

    void updateJob(int jobId, Job updated);

    void deleteJob(int jobId);

    void archiveJob(int jobId);

    ApplyResult applyToJob(int jobId, String coverLetter);
}

