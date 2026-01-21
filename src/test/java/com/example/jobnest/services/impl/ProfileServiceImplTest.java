package com.example.jobnest.services.impl;

import com.example.jobnest.dto.request.ProfileUpdateRequest;
import com.example.jobnest.entity.JobSeekerProfile;
import com.example.jobnest.entity.RecruiterProfile;
import com.example.jobnest.repository.JobSeekerProfileRepository;
import com.example.jobnest.repository.RecruiterProfileRepository;
import com.example.jobnest.services.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileServiceImplTest {

    @Mock
    private JobSeekerProfileRepository jobSeekerProfileRepository;

    @Mock
    private RecruiterProfileRepository recruiterProfileRepository;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private ProfileServiceImpl profileService;

    @BeforeEach
    void setUp() {
        profileService.setSelf(profileService);
    }

    @Test
    void updateJobSeekerProfile_updatesFieldsAndSkills() {
        JobSeekerProfile profile = new JobSeekerProfile();
        profile.setSkills(new ArrayList<>());
        when(jobSeekerProfileRepository.findById(1)).thenReturn(Optional.of(profile));
        when(jobSeekerProfileRepository.save(any(JobSeekerProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProfileUpdateRequest request = new ProfileUpdateRequest();
        request.setFirstName("Jane");
        request.setLastName("Doe");
        request.setCity("Baku");
        request.setResumeUrl("http://resume");

        MockMultipartFile image = new MockMultipartFile("image", "img.png", "image/png", new byte[] {1, 2});
        request.setImage(image);
        when(fileStorageService.storeProfilePhoto(image, 1)).thenReturn(new byte[] {9, 9});

        List<ProfileUpdateRequest.SkillRequest> skills = List.of(
                new ProfileUpdateRequest.SkillRequest("Java", "Mid", "3"));
        request.setSkills(skills);

        JobSeekerProfile updated = profileService.updateJobSeekerProfile(request, 1);

        assertEquals("Jane", updated.getFirstName());
        assertEquals("Doe", updated.getLastName());
        assertEquals("Baku", updated.getCity());
        assertEquals("http://resume", updated.getResume());
        assertEquals(1, updated.getSkills().size());
        assertEquals("Java", updated.getSkills().get(0).getName());
    }

    @Test
    void updateRecruiterProfile_updatesBasicFields() {
        RecruiterProfile profile = new RecruiterProfile();
        when(recruiterProfileRepository.findById(2)).thenReturn(Optional.of(profile));
        when(recruiterProfileRepository.save(any(RecruiterProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProfileUpdateRequest request = new ProfileUpdateRequest();
        request.setFirstName("Alice");
        request.setLastName("Smith");
        request.setCompany("Acme");
        request.setCity("Ganja");

        RecruiterProfile updated = profileService.updateRecruiterProfile(request, 2);

        assertEquals("Alice", updated.getFirstName());
        assertEquals("Smith", updated.getLastName());
        assertEquals("Acme", updated.getCompany());
        assertEquals("Ganja", updated.getCity());
    }
}
