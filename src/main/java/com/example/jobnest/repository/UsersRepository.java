package com.example.jobnest.repository;

import com.example.jobnest.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Integer> {

    Optional<Users> findByEmail(String email);

    @org.springframework.data.jpa.repository.Query("SELECT u FROM Users u LEFT JOIN FETCH u.recruiterProfile rp LEFT JOIN FETCH u.jobSeekerProfile jsp")
    java.util.List<Users> findAllWithProfiles();
}
