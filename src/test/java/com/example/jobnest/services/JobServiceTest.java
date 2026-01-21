package com.example.jobnest.services;

import com.example.jobnest.entity.Job;
import com.example.jobnest.repository.JobRepository;
import com.example.jobnest.services.impl.JobServiceImpl;
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
class JobServiceTest {

    @Mock
    private JobRepository jobRepository;

    @InjectMocks
    private JobServiceImpl jobService;

    @Test
    void save_ShouldReturnSavedJob() {
        Job job = new Job();
        job.setTitle("Java Developer");

        when(jobRepository.save(any(Job.class))).thenReturn(job);

        Job result = jobService.save(job);

        assertNotNull(result);
        assertEquals("Java Developer", result.getTitle());
        verify(jobRepository).save(job);
    }

    @Test
    void findAllActiveJobs_ShouldReturnList() {
        Job job1 = new Job();
        job1.setActive(true);
        when(jobRepository.findByActiveTrue()).thenReturn(Arrays.asList(job1));

        List<Job> result = jobService.findAllActiveJobs();

        assertEquals(1, result.size());
        verify(jobRepository).findByActiveTrue();
    }

    @Test
    void searchJobs_ShouldHandleNullInputs() {
        // Arrange
        when(jobRepository.searchJobs("", "", "")).thenReturn(Collections.emptyList());

        // Act
        jobService.searchJobs(null, null, null);

        // Assert
        verify(jobRepository).searchJobs("", "", "");
    }

    @Test
    void archive_ShouldSetJobInactive() {
        // Arrange
        int jobId = 1;
        Job job = new Job();
        job.setJobId(jobId);
        job.setActive(true);

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(jobRepository.save(any(Job.class))).thenReturn(job);

        // Act
        jobService.archive(jobId);

        // Assert
        assertFalse(job.isActive());
        verify(jobRepository).save(job);
    }
}
