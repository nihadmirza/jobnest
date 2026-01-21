package com.example.jobnest.services.impl;

import com.example.jobnest.dto.response.AdminDashboardDTO;
import com.example.jobnest.dto.response.AdminReportsDTO;
import com.example.jobnest.entity.Job;
import com.example.jobnest.entity.RecruiterProfile;
import com.example.jobnest.entity.Users;
import com.example.jobnest.services.AdminReportService;
import com.example.jobnest.services.JobSeekerApplyService;
import com.example.jobnest.services.JobService;
import com.example.jobnest.services.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of AdminReportService.
 */
@Service
@RequiredArgsConstructor
public class AdminReportServiceImpl implements AdminReportService {

        private final UsersService usersService;
        private final JobService jobService;
        private final JobSeekerApplyService jobSeekerApplyService;

        @Override
        public AdminDashboardDTO getDashboardStats() {
                long totalUsers = usersService.getTotalUsersCount();
                long activeJobs = jobService.getActiveJobsCount();
                long totalApplications = jobSeekerApplyService.getTotalApplicationsCount();

                return AdminDashboardDTO.builder()
                                .totalUsers(totalUsers)
                                .activeJobs(activeJobs)
                                .totalApplications(totalApplications)
                                .build();
        }

        @Override
        public AdminReportsDTO getDetailedReports() {
                // User statistics by role
                long totalUsers = usersService.getTotalUsersCount();
                long recruiters = usersService.getAllUsers().stream()
                                .filter(u -> u.getUserTypeId() != null && u.getUserTypeId().getUserTypeId() == 1)
                                .count();
                long jobSeekers = usersService.getAllUsers().stream()
                                .filter(u -> u.getUserTypeId() != null && u.getUserTypeId().getUserTypeId() == 2)
                                .count();

                // Job and application stats
                long totalJobs = jobService.findAllJobs().size();
                long activeJobs = jobService.getActiveJobsCount();
                long totalApplications = jobSeekerApplyService.getTotalApplicationsCount();

                // Average applications per job
                double avgApplications = totalJobs > 0 ? (double) totalApplications / totalJobs : 0;

                // Top 5 most applied jobs
                List<Map.Entry<Job, Long>> topJobs = jobSeekerApplyService.getAllApplications().stream()
                                .collect(Collectors.groupingBy(
                                                app -> app.getJob(),
                                                Collectors.counting()))
                                .entrySet().stream()
                                .sorted(Map.Entry.<Job, Long>comparingByValue().reversed())
                                .limit(5)
                                .toList();

                // Top 5 recruiters by job postings
                List<Map.Entry<RecruiterProfile, Long>> topRecruiters = jobService.findAllJobs().stream()
                                .collect(Collectors.groupingBy(
                                                Job::getRecruiter,
                                                Collectors.counting()))
                                .entrySet().stream()
                                .sorted(Map.Entry.<RecruiterProfile, Long>comparingByValue().reversed())
                                .limit(5)
                                .toList();

                // Top 5 job seekers by applications
                List<Map.Entry<Users, Long>> topJobSeekers = jobSeekerApplyService.getAllApplications().stream()
                                .collect(Collectors.groupingBy(
                                                app -> app.getUser(),
                                                Collectors.counting()))
                                .entrySet().stream()
                                .sorted(Map.Entry.<Users, Long>comparingByValue().reversed())
                                .limit(5)
                                .toList();

                return AdminReportsDTO.builder()
                                .totalUsers(totalUsers)
                                .recruiters(recruiters)
                                .jobSeekers(jobSeekers)
                                .totalJobs(totalJobs)
                                .activeJobs(activeJobs)
                                .totalApplications(totalApplications)
                                .avgApplications(String.format("%.1f", avgApplications))
                                .topJobs(topJobs)
                                .topRecruiters(topRecruiters)
                                .topJobSeekers(topJobSeekers)
                                .build();
        }
}
