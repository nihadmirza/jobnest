package com.example.jobnest.services.impl;

import com.example.jobnest.common.UserType;
import com.example.jobnest.entity.Job;
import com.example.jobnest.entity.RecruiterProfile;
import com.example.jobnest.entity.Users;
import com.example.jobnest.exception.UnauthorizedException;
import com.example.jobnest.exception.ValidationException;
import com.example.jobnest.services.AuthenticationService;
import com.example.jobnest.services.JobPageService;
import com.example.jobnest.services.JobSeekerApplyService;
import com.example.jobnest.services.JobService;
import com.example.jobnest.services.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JobPageServiceImpl implements JobPageService {

    private static final String JOBS_LIST_VIEW = "jobs-list";
    private static final String JOB_DETAILS_VIEW = "job-details";
    private static final String JOB_FORM_VIEW = "job-form";
    private static final String RECRUITER_JOBS_VIEW = "recruiter-jobs-list";

    private static final String PROFILE_ATTR = "profile";

    private final JobService jobService;
    private final JobSeekerApplyService jobSeekerApplyService;
    private final AuthenticationService authenticationService;
    private final ProfileService profileService;

    @Override
    @Transactional(readOnly = true)
    public PageResult globalSearch(String job, String location) {
        List<Job> jobs = jobService.searchJobs(job, location, null);
        Map<String, Object> model = new HashMap<>();
        model.put("jobs", jobs);
        model.put("job", job);
        model.put("location", location);
        return new PageResult(JOBS_LIST_VIEW, model);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult listAllJobs(String job, String location, String type) {
        boolean hasFilter = (job != null && !job.isEmpty())
                || (location != null && !location.isEmpty())
                || (type != null && !type.isEmpty());

        List<Job> jobs = hasFilter
                ? jobService.searchJobs(job, location, type)
                : jobService.findAllActiveJobs();

        Map<String, Object> model = new HashMap<>();
        model.put("jobs", jobs);
        model.put("keyword", job);
        model.put("location", location);
        model.put("type", type);
        return new PageResult(JOBS_LIST_VIEW, model);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult viewJob(int jobId) {
        Optional<Job> jobOpt = jobService.findById(jobId);
        if (jobOpt.isEmpty() || !jobOpt.get().isActive()) {
            return new PageResult("redirect:/jobs", Map.of());
        }

        Users user = authenticationService.getCurrentAuthenticatedUser();
        boolean isAuthenticated = user != null;

        boolean isJobSeeker = isAuthenticated
                && UserType.fromUsersType(user.getUserTypeId()).orElse(null) == UserType.JOB_SEEKER;

        boolean hasApplied = isJobSeeker && jobSeekerApplyService.hasUserApplied(jobId, user.getUserId());

        Map<String, Object> model = new HashMap<>();
        model.put("job", jobOpt.get());
        model.put("isAuthenticated", isAuthenticated);
        model.put("isCandidate", isJobSeeker);
        model.put("hasApplied", hasApplied);
        return new PageResult(JOB_DETAILS_VIEW, model);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult listRecruiterJobs() {
        Users user = requireAuthenticatedUser();
        RecruiterProfile recruiterProfile = profileService.getRecruiterProfile(user.getUserId());
        List<Job> jobs = jobService.findJobsByRecruiterId(recruiterProfile.getUserAccountId());

        Map<String, Object> model = new HashMap<>();
        model.put("jobs", jobs);
        model.put(PROFILE_ATTR, recruiterProfile);
        return new PageResult(RECRUITER_JOBS_VIEW, model);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult showCreateJobForm() {
        Users user = requireAuthenticatedUser();
        RecruiterProfile profile = profileService.getRecruiterProfile(user.getUserId());

        Map<String, Object> model = new HashMap<>();
        model.put(PROFILE_ATTR, profile);
        model.put("job", new Job());
        return new PageResult(JOB_FORM_VIEW, model);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult showCreateJobForm(Job jobWithErrors) {
        Users user = requireAuthenticatedUser();
        RecruiterProfile profile = profileService.getRecruiterProfile(user.getUserId());

        Map<String, Object> model = new HashMap<>();
        model.put(PROFILE_ATTR, profile);
        model.put("job", jobWithErrors);
        return new PageResult(JOB_FORM_VIEW, model);
    }

    @Override
    @Transactional
    public int createRecruiterJobDraft(Job job) {
        Users user = requireAuthenticatedUser();
        RecruiterProfile recruiterProfile = profileService.getRecruiterProfile(user.getUserId());

        job.setRecruiter(recruiterProfile);
        job.setPaymentStatus("PENDING");
        job.setActive(false);

        return jobService.save(job).getJobId();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult showEditJobForm(int jobId) {
        Users user = requireAuthenticatedUser();
        RecruiterProfile profile = profileService.getRecruiterProfile(user.getUserId());

        Optional<Job> jobOpt = jobService.findById(jobId);
        if (jobOpt.isEmpty()) {
            return new PageResult("redirect:/recruiter/jobs", Map.of());
        }

        Map<String, Object> model = new HashMap<>();
        model.put(PROFILE_ATTR, profile);
        model.put("job", jobOpt.get());
        return new PageResult(JOB_FORM_VIEW, model);
    }

    @Override
    @Transactional
    public void updateJob(int jobId, Job updated) {
        Job existing = jobService.findById(jobId)
                .orElseThrow(() -> new ValidationException("Job not found."));

        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setLocation(updated.getLocation());
        existing.setCity(updated.getCity());
        existing.setState(updated.getState());
        existing.setCountry(updated.getCountry());
        existing.setEmploymentType(updated.getEmploymentType());
        existing.setSalary(updated.getSalary());
        existing.setActive(updated.isActive());

        jobService.update(existing);
    }

    @Override
    @Transactional
    public void deleteJob(int jobId) {
        jobService.deleteById(jobId);
    }

    @Override
    @Transactional
    public void archiveJob(int jobId) {
        jobService.archive(jobId);
    }

    @Override
    @Transactional
    public ApplyResult applyToJob(int jobId, String coverLetter) {
        Users user = authenticationService.getCurrentAuthenticatedUser();
        if (user == null) {
            return new ApplyResult("redirect:/login", "error", "Please login to apply for jobs");
        }

        UserType type = UserType.fromUsersType(user.getUserTypeId()).orElse(null);
        if (type != UserType.JOB_SEEKER) {
            String redirectTo = (type == UserType.RECRUITER) ? "redirect:/dashboard" : "redirect:/login";
            return new ApplyResult(redirectTo, "error", "Only Job Seekers can apply for jobs.");
        }

        jobSeekerApplyService.applyToJob(jobId, user.getUserId(), coverLetter);
        return new ApplyResult("redirect:/jobs/" + jobId, "success", "Application submitted successfully");
    }

    private Users requireAuthenticatedUser() {
        Users user = authenticationService.getCurrentAuthenticatedUser();
        if (user == null) {
            throw new UnauthorizedException("Authentication required.");
        }
        return user;
    }
}

