package com.example.jobnest.services.impl;

import com.example.jobnest.dto.response.RecruiterApplicationStatsDTO;
import com.example.jobnest.entity.Job;
import com.example.jobnest.entity.JobSeekerApply;
import com.example.jobnest.entity.JobSeekerProfile;
import com.example.jobnest.entity.RecruiterProfile;
import com.example.jobnest.entity.Users;
import com.example.jobnest.exception.UnauthorizedException;
import com.example.jobnest.repository.JobSeekerProfileRepository;
import com.example.jobnest.services.JobSeekerApplyService;
import com.example.jobnest.services.JobService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationManagementServiceImplTest {

    @Mock
    private JobSeekerApplyService jobSeekerApplyService;

    @Mock
    private JobService jobService;

    @Mock
    private JobSeekerProfileRepository jobSeekerProfileRepository;

    @InjectMocks
    private ApplicationManagementServiceImpl applicationManagementService;

    @BeforeEach
    void setUp() {
        applicationManagementService.setSelf(applicationManagementService);
    }

    @Test
    void getApplicationsWithStats_buildsStatsForJobFilter() {
        Users user = new Users();
        user.setUserId(5);
        RecruiterProfile recruiter = new RecruiterProfile();
        recruiter.setUserAccountId(9);
        Job job = new Job();
        job.setRecruiter(recruiter);
        JobSeekerApply application = new JobSeekerApply();
        application.setUser(user);
        application.setJob(job);

        when(jobSeekerApplyService.getApplicationsByRecruiterIdAndJobId(9, 2)).thenReturn(List.of(application));
        when(jobService.findJobsByRecruiterId(9)).thenReturn(List.of(job));
        when(jobSeekerProfileRepository.findById(5)).thenReturn(Optional.of(new JobSeekerProfile()));
        when(jobSeekerApplyService.getApplicationCountByRecruiterId(9)).thenReturn(1L);
        when(jobSeekerApplyService.getApplicationCountByRecruiterIdAndStatus(9, "Pending")).thenReturn(1L);
        when(jobSeekerApplyService.getApplicationCountByRecruiterIdAndStatus(9, "Reviewed")).thenReturn(0L);
        when(jobSeekerApplyService.getApplicationCountByRecruiterIdAndStatus(9, "Accepted")).thenReturn(0L);
        when(jobSeekerApplyService.getApplicationCountByRecruiterIdAndStatus(9, "Rejected")).thenReturn(0L);

        RecruiterApplicationStatsDTO dto = applicationManagementService.getApplicationsWithStats(9, 2, null);

        assertEquals(1, dto.getApplications().size());
        assertEquals(1, dto.getCandidateProfilesMap().size());
        assertEquals(1L, dto.getTotalApplications());
    }

    @Test
    void getApplicationForRecruiter_throwsWhenNotOwner() {
        Users user = new Users();
        user.setUserId(5);
        RecruiterProfile recruiter = new RecruiterProfile();
        recruiter.setUserAccountId(9);
        Job job = new Job();
        job.setRecruiter(recruiter);
        JobSeekerApply application = new JobSeekerApply();
        application.setUser(user);
        application.setJob(job);

        when(jobSeekerApplyService.findById(1)).thenReturn(Optional.of(application));

        assertThrows(UnauthorizedException.class,
                () -> applicationManagementService.getApplicationForRecruiter(1, 99));
    }
}
