package com.example.jobnest.services.impl;

import com.example.jobnest.dto.response.HomePageDataDTO;
import com.example.jobnest.entity.Job;
import com.example.jobnest.entity.Users;
import com.example.jobnest.entity.UsersType;
import com.example.jobnest.services.JobService;
import com.example.jobnest.services.UsersService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HomePageServiceImplTest {

    @Mock
    private JobService jobService;

    @Mock
    private UsersService usersService;

    @InjectMocks
    private HomePageServiceImpl homePageService;

    @Test
    void getHomePageData_buildsFeaturedAndCounts() {
        List<Job> jobs = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Job featured = new Job();
            featured.setFeatured(true);
            jobs.add(featured);
        }
        for (int i = 0; i < 5; i++) {
            Job regular = new Job();
            regular.setFeatured(false);
            jobs.add(regular);
        }
        when(jobService.findAllActiveJobs()).thenReturn(jobs);

        Users recruiter = new Users();
        UsersType type = new UsersType();
        type.setUserTypeId(1);
        recruiter.setUserTypeId(type);
        when(usersService.getAllUsers()).thenReturn(List.of(recruiter));

        HomePageDataDTO dto = homePageService.getHomePageData();

        assertEquals(6, dto.getFeaturedJobs().size());
        assertEquals(7, dto.getTotalJobs());
        assertEquals(1, dto.getTotalRecruiters());
    }
}
