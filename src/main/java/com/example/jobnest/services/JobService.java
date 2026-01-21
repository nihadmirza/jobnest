package com.example.jobnest.services;

import com.example.jobnest.entity.Job;

import java.util.List;
import java.util.Optional;

public interface JobService {

    Job save(Job job);

    Optional<Job> findById(int id);

    List<Job> findAllActiveJobs();

    List<Job> findJobsByRecruiterId(int recruiterId);

    List<Job> searchJobs(String keyword, String location, String type);

    void deleteById(int id);

    Job update(Job job);

    void archive(int id);

    long getActiveJobsCount();

    List<Job> findAllJobs();
}
