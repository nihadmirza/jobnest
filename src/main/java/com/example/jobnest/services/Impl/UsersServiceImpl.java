package com.example.jobnest.services.impl;

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
import com.example.jobnest.services.UsersService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsersServiceImpl implements UsersService {

    private final UsersRepository usersRepository;
    private final JobSeekerProfileRepository jobSeekerProfileRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final UsersTypeRepository usersTypeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Users registerUser(UserRegistrationRequest request) {
        // Check if email already exists (normalize to match storage format)
        Optional<Users> existingUser = usersRepository.findByEmail(request.getEmail().toLowerCase());
        if (existingUser.isPresent()) {
            throw new ValidationException("Bu email ünvanı ilə artıq qeydiyyat mövcuddur.");
        }

        // Find user type based on role
        int userTypeId = request.getRole().equals("RECRUITER") ? 1 : 2;
        UsersType userType = usersTypeRepository.findById(userTypeId)
                .orElseThrow(() -> new ValidationException("Yanlış istifadəçi tipi seçildi."));

        // Create new user
        Users users = new Users();
        users.setEmail(request.getEmail().toLowerCase());
        users.setPassword(passwordEncoder.encode(request.getPassword()));
        users.setActive(true);
        users.setRegistrationDate(new Date(System.currentTimeMillis()));
        users.setUserTypeId(userType);

        // Save user
        Users savedUser = usersRepository.save(users);

        // Create appropriate profile
        if (userTypeId == 1) {
            // Recruiter profile
            RecruiterProfile recruiterProfile = new RecruiterProfile(savedUser);
            recruiterProfile.setFirstName(request.getFirstName());
            recruiterProfile.setLastName(request.getLastName());
            recruiterProfile.setCompany(request.getCompany());
            recruiterProfileRepository.save(recruiterProfile);
        } else {
            // Job seeker profile
            JobSeekerProfile jobSeekerProfile = new JobSeekerProfile(savedUser);
            jobSeekerProfile.setFirstName(request.getFirstName());
            jobSeekerProfile.setLastName(request.getLastName());
            jobSeekerProfileRepository.save(jobSeekerProfile);
        }

        return savedUser;
    }

    @Override

    /**
     * Creates a new user with the given details.
     * 
     * @deprecated Use {@link #registerUser(UserRegistrationRequest)} instead.
     *             This method will be removed in version 2.0.0.
     * 
     * @param users     The user entity
     * @param firstName User's first name
     * @param lastName  User's last name
     * @param company   Company name (if recruiter)
     * @return Created user entity
     */
    @Deprecated(since = "1.5.0", forRemoval = true)
    @SuppressWarnings("deprecated")
    @Transactional(rollbackFor = Exception.class)
    public Users addNew(Users users, String firstName, String lastName, String company) {
        // email yoxlanisi
        Optional<Users> existingUser = usersRepository.findByEmail(users.getEmail().toLowerCase());
        if (existingUser.isPresent()) {
            throw new ValidationException("Bu email ünvanı ilə artıq qeydiyyat mövcuddur.");
        }

        users.setActive(true);
        users.setRegistrationDate(new Date(System.currentTimeMillis()));
        users.setPassword(passwordEncoder.encode(users.getPassword()));

        // esas user in yadda saxlanilmasi
        Users savedUser = usersRepository.save(users);

        if (users.getUserTypeId() == null) {
            throw new ValidationException("İstifadəçi tipi seçilməlidir.");
        }

        int userTypeId = users.getUserTypeId().getUserTypeId();
        if (userTypeId == 1) {
            // Recruiter profile yaradılması
            RecruiterProfile recruiterProfile = new RecruiterProfile(savedUser);
            recruiterProfile.setFirstName(firstName);
            recruiterProfile.setLastName(lastName);
            recruiterProfile.setCompany(company);
            recruiterProfileRepository.save(recruiterProfile);
        } else {
            // Job seeker profile yaradılması
            JobSeekerProfile jobSeekerProfile = new JobSeekerProfile(savedUser);
            jobSeekerProfile.setFirstName(firstName);
            jobSeekerProfile.setLastName(lastName);
            jobSeekerProfileRepository.save(jobSeekerProfile);
        }

        return savedUser;
    }

    @Override
    public Optional<Users> getUserByEmail(String email) {
        return usersRepository.findByEmail(email.toLowerCase());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<JobSeekerProfile> getJobSeekerProfileByUserId(int userId) {
        Optional<JobSeekerProfile> profile = jobSeekerProfileRepository.findById(userId);
        // Force initialization of skills to avoid lazy loading issues
        if (profile.isPresent() && profile.get().getSkills() != null) {
            log.debug("Skills count for new profile: {}", profile.get().getSkills().size());
        }
        return profile;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RecruiterProfile> getRecruiterProfileByUserId(int userId) {
        return recruiterProfileRepository.findById(userId);
    }

    @Override
    @Transactional
    public JobSeekerProfile updateJobSeekerProfile(JobSeekerProfile profile) {
        return jobSeekerProfileRepository.save(profile);
    }

    @Override
    @Transactional
    public RecruiterProfile updateRecruiterProfile(RecruiterProfile profile) {
        return recruiterProfileRepository.save(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public Users getCurrentUser() {
        org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {
            return null;
        }
        String email = authentication.getName();
        return usersRepository.findByEmail(email.toLowerCase()).orElse(null);
    }

    @Override
    public long getTotalUsersCount() {
        return usersRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Users> getAllUsers() {
        return usersRepository.findAll();
    }
}
