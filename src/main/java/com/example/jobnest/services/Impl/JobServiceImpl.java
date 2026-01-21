package com.example.jobnest.services.impl;

import com.example.jobnest.entity.Job;
import com.example.jobnest.repository.JobRepository;
import com.example.jobnest.services.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;

    @Override
    @Transactional
    public Job save(Job job) {
        return jobRepository.save(job);
    }

    @Override
    public Optional<Job> findById(int id) {
        return jobRepository.findById(id);
    }

    @Override
    public List<Job> findAllActiveJobs() {
        return jobRepository.findByActiveTrue();
    }

    @Override
    public List<Job> findJobsByRecruiterId(int recruiterId) {
        return jobRepository.findByRecruiter_UserAccountId(recruiterId);
    }

    @Override
    public List<Job> searchJobs(String keyword, String location, String type) {
        if (keyword == null || keyword.trim().isEmpty()) {
            keyword = "";
        }
        if (location == null || location.trim().isEmpty()) {
            location = "";
        }
        if (type == null || type.trim().isEmpty()) {
            type = "";
        }
        return jobRepository.searchJobs(keyword, location, type);
    }

    @Override
    @Transactional
    public void deleteById(int id) {
        jobRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Job update(Job job) {
        return jobRepository.save(job);
    }

    @Override
    @Transactional
    public void archive(int id) {
        Optional<Job> jobOptional = jobRepository.findById(id);
        if (jobOptional.isPresent()) {
            Job job = jobOptional.get();
            job.setActive(false);
            jobRepository.save(job);
        }
    }

    @Override
    public long getActiveJobsCount() {
        return jobRepository.findByActiveTrue().size();
    }

    @Override
    public List<Job> findAllJobs() {
        return jobRepository.findAll();
    }
}
