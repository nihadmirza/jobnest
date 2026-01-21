package com.example.jobnest.repository;

import com.example.jobnest.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Integer> {

        @Query("SELECT j FROM Job j LEFT JOIN FETCH j.recruiter r WHERE j.active = true")
        List<Job> findByActiveTrue();

        @Query("SELECT j FROM Job j LEFT JOIN FETCH j.recruiter r WHERE r.userAccountId = :recruiterId")
        List<Job> findByRecruiter_UserAccountId(int recruiterId);

        @Query("SELECT j FROM Job j LEFT JOIN FETCH j.recruiter r " +
                        "WHERE j.active = true AND " +
                        "(:keyword IS NULL OR :keyword = '' OR " +
                        " LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                        " LOWER(j.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                        " LOWER(r.company) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
                        "(:location IS NULL OR :location = '' OR " +
                        " LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%')) OR " +
                        " LOWER(j.city) LIKE LOWER(CONCAT('%', :location, '%')) OR " +
                        " LOWER(j.state) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
                        "(:type IS NULL OR :type = '' OR j.employmentType = :type)")
        List<Job> searchJobs(@Param("keyword") String keyword,
                        @Param("location") String location,
                        @Param("type") String type);
}
