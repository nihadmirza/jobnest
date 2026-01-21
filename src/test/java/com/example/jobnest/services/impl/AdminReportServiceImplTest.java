package com.example.jobnest.services.impl;

import com.example.jobnest.dto.response.AdminDashboardDTO;
import com.example.jobnest.dto.response.AdminReportsDTO;
import com.example.jobnest.entity.Job;
import com.example.jobnest.entity.JobSeekerApply;
import com.example.jobnest.entity.RecruiterProfile;
import com.example.jobnest.entity.Users;
import com.example.jobnest.entity.UsersType;
import com.example.jobnest.services.JobSeekerApplyService;
import com.example.jobnest.services.JobService;
import com.example.jobnest.services.UsersService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminReportServiceImplTest {

    @Mock
    private UsersService usersService;

    @Mock
    private JobService jobService;

    @Mock
    private JobSeekerApplyService jobSeekerApplyService;

    @InjectMocks
    private AdminReportServiceImpl adminReportService;

    @Test
    void getDashboardStats_returnsCounts() {
        when(usersService.getTotalUsersCount()).thenReturn(12L);
        when(jobService.getActiveJobsCount()).thenReturn(4L);
        when(jobSeekerApplyService.getTotalApplicationsCount()).thenReturn(19L);

        AdminDashboardDTO dto = adminReportService.getDashboardStats();

        assertEquals(12L, dto.getTotalUsers());
        assertEquals(4L, dto.getActiveJobs());
        assertEquals(19L, dto.getTotalApplications());
    }

    @Test
    void getDetailedReports_calculatesStatsAndTopLists() {
        UsersType recruiterType = new UsersType();
        recruiterType.setUserTypeId(1);
        UsersType seekerType = new UsersType();
        seekerType.setUserTypeId(2);

        Users recruiterUser = new Users();
        recruiterUser.setUserId(1);
        recruiterUser.setUserTypeId(recruiterType);

        Users seekerUser1 = new Users();
        seekerUser1.setUserId(2);
        seekerUser1.setUserTypeId(seekerType);

        Users seekerUser2 = new Users();
        seekerUser2.setUserId(3);
        seekerUser2.setUserTypeId(seekerType);

        when(usersService.getAllUsers()).thenReturn(List.of(recruiterUser, seekerUser1, seekerUser2));
        when(usersService.getTotalUsersCount()).thenReturn(3L);

        RecruiterProfile recruiterProfile = new RecruiterProfile();
        recruiterProfile.setUserAccountId(10);
        recruiterProfile.setCompany("Acme");

        Job job1 = new Job();
        job1.setJobId(101);
        job1.setRecruiter(recruiterProfile);

        Job job2 = new Job();
        job2.setJobId(102);
        job2.setRecruiter(recruiterProfile);

        when(jobService.findAllJobs()).thenReturn(List.of(job1, job2));
        when(jobService.getActiveJobsCount()).thenReturn(1L);

        JobSeekerApply app1 = new JobSeekerApply();
        app1.setJob(job1);
        app1.setUser(seekerUser1);

        JobSeekerApply app2 = new JobSeekerApply();
        app2.setJob(job1);
        app2.setUser(seekerUser1);

        JobSeekerApply app3 = new JobSeekerApply();
        app3.setJob(job2);
        app3.setUser(seekerUser2);

        when(jobSeekerApplyService.getAllApplications()).thenReturn(List.of(app1, app2, app3));
        when(jobSeekerApplyService.getTotalApplicationsCount()).thenReturn(3L);

        AdminReportsDTO dto = adminReportService.getDetailedReports();

        assertEquals(3L, dto.getTotalUsers());
        assertEquals(1L, dto.getRecruiters());
        assertEquals(2L, dto.getJobSeekers());
        assertEquals(2L, dto.getTotalJobs());
        assertEquals(1L, dto.getActiveJobs());
        assertEquals(3L, dto.getTotalApplications());
        assertEquals("1.5", dto.getAvgApplications());
        assertEquals(2, dto.getTopJobs().size());
        assertEquals(1, dto.getTopRecruiters().size());
        assertEquals(2, dto.getTopJobSeekers().size());
    }

    @Test
    void getDetailedReports_handlesZeroJobs() {
        when(usersService.getAllUsers()).thenReturn(List.of());
        when(usersService.getTotalUsersCount()).thenReturn(0L);
        when(jobService.findAllJobs()).thenReturn(List.of());
        when(jobService.getActiveJobsCount()).thenReturn(0L);
        when(jobSeekerApplyService.getAllApplications()).thenReturn(List.of());
        when(jobSeekerApplyService.getTotalApplicationsCount()).thenReturn(0L);

        AdminReportsDTO dto = adminReportService.getDetailedReports();

        assertEquals(0L, dto.getTotalJobs());
        assertEquals("0.0", dto.getAvgApplications());
        assertTrue(dto.getTopJobs().isEmpty());
        assertTrue(dto.getTopRecruiters().isEmpty());
        assertTrue(dto.getTopJobSeekers().isEmpty());
    }
}
