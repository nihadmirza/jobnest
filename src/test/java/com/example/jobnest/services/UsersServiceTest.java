package com.example.jobnest.services;

import com.example.jobnest.dto.request.UserRegistrationRequest;
import com.example.jobnest.entity.JobSeekerProfile;
import com.example.jobnest.entity.RecruiterProfile;
import com.example.jobnest.entity.Users;
import com.example.jobnest.entity.UsersType;
import com.example.jobnest.exception.ValidationException;
import com.example.jobnest.repository.JobSeekerProfileRepository;
import com.example.jobnest.repository.RecruiterProfileRepository;
import com.example.jobnest.repository.UsersRepository;
import com.example.jobnest.repository.UsersTypeRepository;
import com.example.jobnest.services.impl.UsersServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsersServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private JobSeekerProfileRepository jobSeekerProfileRepository;

    @Mock
    private RecruiterProfileRepository recruiterProfileRepository;

    @Mock
    private UsersTypeRepository usersTypeRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsersServiceImpl usersService;

    @Test
    void registerUser_ShouldCreateJobSeeker_WhenRoleIsCandidate() {
        // Arrange
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setRole("CANDIDATE");
        request.setFirstName("John");
        request.setLastName("Doe");

        UsersType jobSeekerType = new UsersType();
        jobSeekerType.setUserTypeId(2);

        when(usersRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(usersTypeRepository.findById(2)).thenReturn(Optional.of(jobSeekerType));
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");

        Users savedUser = new Users();
        savedUser.setUserId(1);
        savedUser.setEmail(request.getEmail());
        when(usersRepository.save(any(Users.class))).thenReturn(savedUser);

        // Act
        Users result = usersService.registerUser(request);

        // Assert
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        verify(usersRepository).save(any(Users.class));
        verify(jobSeekerProfileRepository).save(any(JobSeekerProfile.class));
        verify(recruiterProfileRepository, never()).save(any(RecruiterProfile.class));
    }

    @Test
    void registerUser_ShouldCreateRecruiter_WhenRoleIsRecruiter() {
        // Arrange
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setEmail("recruiter@example.com");
        request.setPassword("password123");
        request.setRole("RECRUITER");
        request.setCompany("Tech Corp");
        request.setFirstName("Jane");
        request.setLastName("Smith");

        UsersType recruiterType = new UsersType();
        recruiterType.setUserTypeId(1);

        when(usersRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(usersTypeRepository.findById(1)).thenReturn(Optional.of(recruiterType));
        when(usersRepository.save(any(Users.class))).thenReturn(new Users());

        // Act
        usersService.registerUser(request);

        // Assert
        verify(recruiterProfileRepository).save(any(RecruiterProfile.class));
        verify(jobSeekerProfileRepository, never()).save(any(JobSeekerProfile.class));
    }

    @Test
    void registerUser_ShouldThrowException_WhenEmailExists() {
        // Arrange
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setEmail("existing@example.com");

        when(usersRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(new Users()));

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            usersService.registerUser(request);
        });

        assertEquals("Bu email ünvanı ilə artıq qeydiyyat mövcuddur.", exception.getMessage());
        verify(usersRepository, never()).save(any(Users.class));
    }
}
