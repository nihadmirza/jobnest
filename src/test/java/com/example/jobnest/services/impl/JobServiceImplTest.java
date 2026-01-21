package com.example.jobnest.services.impl;

import com.example.jobnest.entity.Job;
import com.example.jobnest.repository.JobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobServiceImplTest {

    @Mock
    private JobRepository jobRepository;

    @InjectMocks
    private JobServiceImpl jobService;

    private Job job;

    @BeforeEach
    void setUp() {
        job = new Job();
        job.setJobId(1);
        job.setTitle("Developer");
        job.setActive(true);
    }

    @Test
    void save_ShouldReturnSavedJob() {
        when(jobRepository.save(any(Job.class))).thenReturn(job);

        Job savedJob = jobService.save(job);

        assertNotNull(savedJob);
        assertEquals("Developer", savedJob.getTitle());
        verify(jobRepository, times(1)).save(job);
    }

    @Test
    void findById_ShouldReturnJob_WhenFound() {
        when(jobRepository.findById(1)).thenReturn(Optional.of(job));

        Optional<Job> foundJob = jobService.findById(1);

        assertTrue(foundJob.isPresent());
        assertEquals(1, foundJob.get().getJobId());
    }

    @Test
    void findById_ShouldReturnEmpty_WhenNotFound() {
        when(jobRepository.findById(99)).thenReturn(Optional.empty());

        Optional<Job> foundJob = jobService.findById(99);

        assertFalse(foundJob.isPresent());
    }

    @Test
    void findAllActiveJobs_ShouldReturnActiveJobs() {
        when(jobRepository.findByActiveTrue()).thenReturn(Arrays.asList(job));

        List<Job> jobs = jobService.findAllActiveJobs();

        assertEquals(1, jobs.size());
        assertTrue(jobs.get(0).isActive());
    }

    @Test
    void findJobsByRecruiterId_ShouldReturnJobs() {
        when(jobRepository.findByRecruiter_UserAccountId(1)).thenReturn(Arrays.asList(job));

        List<Job> jobs = jobService.findJobsByRecruiterId(1);

        assertEquals(1, jobs.size());
    }

    @Test
    void searchJobs_ShouldReturnJobs_WhenFound() {
        when(jobRepository.searchJobs("dev", "ny", "full-time")).thenReturn(Arrays.asList(job));

        List<Job> jobs = jobService.searchJobs("dev", "ny", "full-time");

        assertEquals(1, jobs.size());
    }

    @Test
    void searchJobs_ShouldHandleNullParams() {
        when(jobRepository.searchJobs("", "", "")).thenReturn(Collections.emptyList());

        List<Job> jobs = jobService.searchJobs(null, null, null);

        assertEquals(0, jobs.size());
        verify(jobRepository).searchJobs("", "", "");
    }

    @Test
    void deleteById_ShouldCallRepository() {
        doNothing().when(jobRepository).deleteById(1);

        jobService.deleteById(1);

        verify(jobRepository, times(1)).deleteById(1);
    }

    @Test
    void update_ShouldSaveJob() {
        when(jobRepository.save(any(Job.class))).thenReturn(job);

        Job updatedJob = jobService.update(job);

        assertNotNull(updatedJob);
    }

    @Test
    void archive_ShouldSetActiveFalse() {
        when(jobRepository.findById(1)).thenReturn(Optional.of(job));
        when(jobRepository.save(any(Job.class))).thenReturn(job);

        jobService.archive(1);

        assertFalse(job.isActive());
        verify(jobRepository).save(job);
    }

    @Test
    void getActiveJobsCount_ShouldReturnCount() {
        when(jobRepository.findByActiveTrue()).thenReturn(Arrays.asList(job, new Job()));

        long count = jobService.getActiveJobsCount();

        assertEquals(2, count);
    }

    @Test
    void findAllJobs_ShouldReturnAllJobs() {
        when(jobRepository.findAll()).thenReturn(Arrays.asList(job));

        List<Job> jobs = jobService.findAllJobs();

        assertEquals(1, jobs.size());
    }
}
