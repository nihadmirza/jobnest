package com.example.jobnest.repository;

import com.example.jobnest.entity.JobSeekerApply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JobSeekerApplyRepository extends JpaRepository<JobSeekerApply, Integer> {

    @Query("SELECT a FROM JobSeekerApply a WHERE a.job.jobId = :jobId AND a.user.userId = :userId")
    Optional<JobSeekerApply> findByJobIdAndUserId(@Param("jobId") int jobId, @Param("userId") int userId);

    boolean existsByJob_JobIdAndUser_UserId(int jobId, int userId);

    @Query("SELECT a FROM JobSeekerApply a LEFT JOIN FETCH a.user u LEFT JOIN FETCH u.jobSeekerProfile LEFT JOIN FETCH a.job j WHERE j.recruiter.userAccountId = :recruiterId ORDER BY a.applyDate DESC")
    List<JobSeekerApply> findByRecruiterId(@Param("recruiterId") int recruiterId);

    @Query("SELECT a FROM JobSeekerApply a LEFT JOIN FETCH a.user u LEFT JOIN FETCH u.jobSeekerProfile LEFT JOIN FETCH a.job j WHERE j.recruiter.userAccountId = :recruiterId AND j.jobId = :jobId ORDER BY a.applyDate DESC")
    List<JobSeekerApply> findByRecruiterIdAndJobId(@Param("recruiterId") int recruiterId, @Param("jobId") int jobId);

    @Query("SELECT a FROM JobSeekerApply a LEFT JOIN FETCH a.user u LEFT JOIN FETCH u.jobSeekerProfile LEFT JOIN FETCH a.job j WHERE j.recruiter.userAccountId = :recruiterId AND a.status = :status ORDER BY a.applyDate DESC")
    List<JobSeekerApply> findByRecruiterIdAndStatus(@Param("recruiterId") int recruiterId,
            @Param("status") String status);

    @Query("SELECT COUNT(a) FROM JobSeekerApply a WHERE a.job.recruiter.userAccountId = :recruiterId")
    long countByRecruiterId(@Param("recruiterId") int recruiterId);

    @Query("SELECT COUNT(a) FROM JobSeekerApply a WHERE a.job.recruiter.userAccountId = :recruiterId AND a.status = :status")
    long countByRecruiterIdAndStatus(@Param("recruiterId") int recruiterId, @Param("status") String status);

    @Query("SELECT a FROM JobSeekerApply a LEFT JOIN FETCH a.job j LEFT JOIN FETCH j.recruiter r WHERE a.user.userId = :userId ORDER BY a.applyDate DESC")
    List<JobSeekerApply> findByUserId(@Param("userId") int userId);
}
