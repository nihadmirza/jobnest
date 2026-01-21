package com.example.jobnest.mapper;

import com.example.jobnest.dto.request.JobCreateRequest;
import com.example.jobnest.dto.request.JobUpdateRequest;
import com.example.jobnest.dto.response.JobResponse;
import com.example.jobnest.entity.Job;
import com.example.jobnest.entity.RecruiterProfile;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Job entities and DTOs.
 */
@Component
public class JobMapper {

    /**
     * Convert JobCreateRequest DTO to Job entity.
     */
    public Job toEntity(JobCreateRequest request, RecruiterProfile recruiter) {
        if (request == null) {
            return null;
        }

        Job job = new Job();
        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setLocation(request.getLocation());
        job.setCity(request.getCity());
        job.setState(request.getState());
        job.setCountry(request.getCountry());
        job.setEmploymentType(request.getEmploymentType());
        job.setSalary(request.getSalary());
        job.setRecruiter(recruiter);
        job.setPaymentStatus("PENDING");
        job.setActive(false); // Jobs are inactive until payment

        return job;
    }

    /**
     * Update job entity from JobUpdateRequest.
     */
    public void updateEntity(Job job, JobUpdateRequest request) {
        if (job == null || request == null) {
            return;
        }

        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setLocation(request.getLocation());
        job.setCity(request.getCity());
        job.setState(request.getState());
        job.setCountry(request.getCountry());
        job.setEmploymentType(request.getEmploymentType());
        job.setSalary(request.getSalary());
        job.setActive(request.getActive());
    }

    /**
     * Convert Job entity to JobResponse DTO.
     */
    public JobResponse toResponse(Job job) {
        if (job == null) {
            return null;
        }

        JobResponse.RecruiterInfo recruiterInfo = null;
        if (job.getRecruiter() != null) {
            recruiterInfo = JobResponse.RecruiterInfo.builder()
                    .recruiterId(job.getRecruiter().getUserAccountId())
                    .firstName(job.getRecruiter().getFirstName())
                    .lastName(job.getRecruiter().getLastName())
                    .company(job.getRecruiter().getCompany())
                    .build();
        }

        return JobResponse.builder()
                .jobId(job.getJobId())
                .title(job.getTitle())
                .description(job.getDescription())
                .location(job.getLocation())
                .city(job.getCity())
                .state(job.getState())
                .country(job.getCountry())
                .employmentType(job.getEmploymentType())
                .salary(job.getSalary())
                .active(job.isActive())
                .paymentStatus(job.getPaymentStatus())
                .recruiter(recruiterInfo)
                .build();
    }
}
