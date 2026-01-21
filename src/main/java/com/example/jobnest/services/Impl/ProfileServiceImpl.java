package com.example.jobnest.services.impl;

import com.example.jobnest.dto.request.ProfileUpdateRequest;
import com.example.jobnest.entity.JobSeekerProfile;
import com.example.jobnest.entity.RecruiterProfile;
import com.example.jobnest.entity.Skills;
import com.example.jobnest.exception.ResourceNotFoundException;
import com.example.jobnest.repository.JobSeekerProfileRepository;
import com.example.jobnest.repository.RecruiterProfileRepository;
import com.example.jobnest.services.FileStorageService;
import com.example.jobnest.services.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of ProfileService.
 * Handles all profile management business logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileServiceImpl implements ProfileService {

    private final JobSeekerProfileRepository jobSeekerProfileRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final FileStorageService fileStorageService;

    private ProfileService self;

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.context.annotation.Lazy
    public void setSelf(ProfileService self) {
        this.self = self;
    }

    @Override
    @Transactional(readOnly = true)
    public JobSeekerProfile getJobSeekerProfile(int userId) {
        JobSeekerProfile profile = jobSeekerProfileRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Job Seeker Profile", userId));

        // Force initialization of skills to avoid lazy loading issues
        if (profile.getSkills() != null) {
            log.debug("Profile skills count: {}", profile.getSkills().size());
        }

        return profile;
    }

    @Override
    @Transactional(readOnly = true)
    public RecruiterProfile getRecruiterProfile(int userId) {
        return recruiterProfileRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter Profile", userId));
    }

    @Override
    @Transactional
    public JobSeekerProfile updateJobSeekerProfile(ProfileUpdateRequest request, int userId) {
        JobSeekerProfile profile = self.getJobSeekerProfile(userId);

        updateBasicInfo(profile, request);
        updateProfilePhoto(profile, request.getImage(), userId);
        updateResume(profile, request, userId);
        updateSkills(profile, request.getSkills());

        return jobSeekerProfileRepository.save(profile);
    }

    private void updateBasicInfo(JobSeekerProfile profile, ProfileUpdateRequest request) {
        if (hasText(request.getFirstName())) {
            profile.setFirstName(request.getFirstName());
        }
        if (hasText(request.getLastName())) {
            profile.setLastName(request.getLastName());
        }
        if (request.getCity() != null) {
            profile.setCity(request.getCity());
        }
        if (request.getState() != null) {
            profile.setState(request.getState());
        }
        if (request.getCountry() != null) {
            profile.setCountry(request.getCountry());
        }
        if (request.getWorkAuthorization() != null) {
            profile.setWorkAuthorization(request.getWorkAuthorization());
        }
        if (request.getEmploymentType() != null) {
            profile.setEmploymentType(request.getEmploymentType());
        }
    }

    private void updateProfilePhoto(JobSeekerProfile profile, org.springframework.web.multipart.MultipartFile image,
            int userId) {
        if (image != null && !image.isEmpty()) {
            byte[] photoBytes = fileStorageService.storeProfilePhoto(image, userId);
            profile.setProfilePhoto(photoBytes);
        }
    }

    private void updateResume(JobSeekerProfile profile, ProfileUpdateRequest request, int userId) {
        if (request.getResumeFile() != null && !request.getResumeFile().isEmpty()) {
            String resumePath = fileStorageService.storeResume(request.getResumeFile(), userId);
            profile.setResume(resumePath);
        } else if (hasText(request.getResumeUrl())) {
            profile.setResume(request.getResumeUrl());
        }
    }

    private void updateSkills(JobSeekerProfile profile, List<ProfileUpdateRequest.SkillRequest> skillRequests) {
        if (skillRequests != null && !skillRequests.isEmpty()) {
            // Clear existing skills
            if (profile.getSkills() != null) {
                profile.getSkills().clear();
            } else {
                profile.setSkills(new ArrayList<>());
            }

            // Add new skills
            for (ProfileUpdateRequest.SkillRequest skillRequest : skillRequests) {
                if (hasText(skillRequest.getName())) {
                    Skills skill = new Skills();
                    skill.setName(skillRequest.getName().trim());
                    skill.setExperienceLevel(skillRequest.getExperienceLevel());
                    skill.setYearsOfExperience(skillRequest.getYearsOfExperience());
                    skill.setJobSeekerProfile(profile);
                    profile.getSkills().add(skill);
                }
            }
        }
    }

    private boolean hasText(String str) {
        return str != null && !str.trim().isEmpty();
    }

    @Override
    @Transactional
    public RecruiterProfile updateRecruiterProfile(ProfileUpdateRequest request, int userId) {
        RecruiterProfile profile = self.getRecruiterProfile(userId);

        // Update basic fields if provided
        if (request.getFirstName() != null && !request.getFirstName().trim().isEmpty()) {
            profile.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null && !request.getLastName().trim().isEmpty()) {
            profile.setLastName(request.getLastName());
        }
        if (request.getCompany() != null && !request.getCompany().trim().isEmpty()) {
            profile.setCompany(request.getCompany());
        }
        if (request.getCity() != null) {
            profile.setCity(request.getCity());
        }
        if (request.getState() != null) {
            profile.setState(request.getState());
        }
        if (request.getCountry() != null) {
            profile.setCountry(request.getCountry());
        }

        // Handle profile photo upload
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            byte[] photoBytes = fileStorageService.storeProfilePhoto(request.getImage(), userId);
            profile.setProfilePhoto(photoBytes);
        }

        return recruiterProfileRepository.save(profile);
    }
}
