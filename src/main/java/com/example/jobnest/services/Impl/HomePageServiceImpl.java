package com.example.jobnest.services.impl;

import com.example.jobnest.dto.response.HomePageDataDTO;
import com.example.jobnest.entity.Job;
import com.example.jobnest.services.HomePageService;
import com.example.jobnest.services.JobService;
import com.example.jobnest.services.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of HomePageService.
 */
@Service
@RequiredArgsConstructor
public class HomePageServiceImpl implements HomePageService {

        private final JobService jobService;
        private final UsersService usersService;

        @Override
        public HomePageDataDTO getHomePageData() {
                // Get featured jobs (limit to 6)
                List<Job> allJobs = jobService.findAllActiveJobs();
                List<Job> featuredJobs = allJobs.stream()
                                .filter(Job::isFeatured)
                                .limit(6)
                                .toList();

                // If not enough featured jobs, add regular active jobs
                if (featuredJobs.size() < 6) {
                        List<Job> regularJobs = allJobs.stream()
                                        .filter(job -> !job.isFeatured())
                                        .limit(6L - featuredJobs.size())
                                        .toList();
                        featuredJobs = java.util.stream.Stream.concat(featuredJobs.stream(), regularJobs.stream())
                                        .toList();
                }

                // Calculate total companies (recruiters)
                long totalCompanies = usersService.getAllUsers().stream()
                                .filter(u -> u.getUserTypeId() != null && u.getUserTypeId().getUserTypeId() == 1)
                                .count();

                return HomePageDataDTO.builder()
                                .featuredJobs(featuredJobs)
                                .totalJobs(allJobs.size())
                                .totalRecruiters(totalCompanies)
                                .totalApplications(0) // Can be added if needed
                                .build();
        }
}
