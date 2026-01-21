package com.example.jobnest.services.impl;

import com.example.jobnest.entity.Job;
import com.example.jobnest.entity.JobSeekerApply;
import com.example.jobnest.entity.Users;
import com.example.jobnest.exception.BusinessException;
import com.example.jobnest.exception.ResourceNotFoundException;
import com.example.jobnest.exception.ValidationException;
import com.example.jobnest.repository.JobRepository;
import com.example.jobnest.repository.JobSeekerApplyRepository;
import com.example.jobnest.repository.UsersRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobSeekerApplyServiceImplTest {

    @Mock
    private JobSeekerApplyRepository jobSeekerApplyRepository;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private JobSeekerApplyServiceImpl jobSeekerApplyService;

    @Test
    void applyToJob_throwsWhenAlreadyApplied() {
        when(jobSeekerApplyRepository.existsByJob_JobIdAndUser_UserId(1, 2)).thenReturn(true);
        assertThrows(BusinessException.class,
                () -> jobSeekerApplyService.applyToJob(1, 2, "cover"));
    }

    @Test
    void applyToJob_throwsWhenJobMissing() {
        when(jobSeekerApplyRepository.existsByJob_JobIdAndUser_UserId(1, 2)).thenReturn(false);
        when(jobRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> jobSeekerApplyService.applyToJob(1, 2, "cover"));
    }

    @Test
    void applyToJob_throwsWhenUserMissing() {
        when(jobSeekerApplyRepository.existsByJob_JobIdAndUser_UserId(1, 2)).thenReturn(false);
        when(jobRepository.findById(1)).thenReturn(Optional.of(new Job()));
        when(usersRepository.findById(2)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> jobSeekerApplyService.applyToJob(1, 2, "cover"));
    }

    @Test
    void applyToJob_throwsWhenJobInactive() {
        Job job = new Job();
        job.setActive(false);
        when(jobSeekerApplyRepository.existsByJob_JobIdAndUser_UserId(1, 2)).thenReturn(false);
        when(jobRepository.findById(1)).thenReturn(Optional.of(job));
        when(usersRepository.findById(2)).thenReturn(Optional.of(new Users()));

        assertThrows(BusinessException.class,
                () -> jobSeekerApplyService.applyToJob(1, 2, "cover"));
    }

    @Test
    void applyToJob_savesApplication() {
        Job job = new Job();
        job.setActive(true);
        Users user = new Users();
        when(jobSeekerApplyRepository.existsByJob_JobIdAndUser_UserId(1, 2)).thenReturn(false);
        when(jobRepository.findById(1)).thenReturn(Optional.of(job));
        when(usersRepository.findById(2)).thenReturn(Optional.of(user));
        when(jobSeekerApplyRepository.save(any(JobSeekerApply.class))).thenAnswer(invocation -> invocation.getArgument(0));

        JobSeekerApply result = jobSeekerApplyService.applyToJob(1, 2, "cover");

        assertEquals("Pending", result.getStatus());
        assertEquals(job, result.getJob());
        assertEquals(user, result.getUser());
    }

    @Test
    void updateApplicationStatus_validatesStatus() {
        JobSeekerApply application = new JobSeekerApply();
        when(jobSeekerApplyRepository.findById(1)).thenReturn(Optional.of(application));
        assertThrows(ValidationException.class,
                () -> jobSeekerApplyService.updateApplicationStatus(1, "Invalid"));
    }
}
