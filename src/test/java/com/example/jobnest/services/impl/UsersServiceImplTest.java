package com.example.jobnest.services.impl;

import com.example.jobnest.dto.request.UserRegistrationRequest;
import com.example.jobnest.entity.Users;
import com.example.jobnest.entity.UsersType;
import com.example.jobnest.exception.ValidationException;
import com.example.jobnest.repository.JobSeekerProfileRepository;
import com.example.jobnest.repository.RecruiterProfileRepository;
import com.example.jobnest.repository.UsersRepository;
import com.example.jobnest.repository.UsersTypeRepository;
import com.example.jobnest.entity.JobSeekerProfile;
import com.example.jobnest.entity.RecruiterProfile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.junit.jupiter.api.AfterEach;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsersServiceImplTest {

    @Mock
    private UsersRepository usersRepository;
    @Mock
    private UsersTypeRepository usersTypeRepository;
    @Mock
    private RecruiterProfileRepository recruiterProfileRepository;
    @Mock
    private JobSeekerProfileRepository jobSeekerProfileRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsersServiceImpl usersService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testRegisterUser_Success() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");
        request.setRole("RECRUITER");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setCompany("Corp");

        UsersType mockType = new UsersType();
        mockType.setUserTypeId(1);
        mockType.setUserTypeName("Recruiter"); // Assuming setters exist. If not, maybe Constructor or Reflection.
        // But UsersType usually has setters.

        when(usersRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(usersTypeRepository.findById(1)).thenReturn(Optional.of(mockType));
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(usersRepository.save(any(Users.class))).thenAnswer(i -> i.getArgument(0));

        Users result = usersService.registerUser(request);

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        verify(recruiterProfileRepository, times(1)).save(any());
    }

    @Test
    void testGetUserByEmail_Found() {
        Users mockUser = new Users();
        mockUser.setEmail("found@example.com");
        when(usersRepository.findByEmail("found@example.com")).thenReturn(Optional.of(mockUser));

        Optional<Users> result = usersService.getUserByEmail("found@example.com");
        assertTrue(result.isPresent());
    }

    @Test
    void registerUser_throwsWhenUserTypeMissing() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setEmail("new@example.com");
        request.setPassword("password");
        request.setRole("RECRUITER");

        when(usersRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(usersTypeRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ValidationException.class, () -> usersService.registerUser(request));
    }

    @Test
    void registerUser_throwsWhenEmailExists() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setEmail("taken@example.com");
        request.setPassword("password");
        request.setRole("RECRUITER");

        when(usersRepository.findByEmail("taken@example.com")).thenReturn(Optional.of(new Users()));

        assertThrows(ValidationException.class, () -> usersService.registerUser(request));
        verify(usersTypeRepository, never()).findById(anyInt());
    }

    @Test
    void getCurrentUser_returnsNullWhenAnonymous() {
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken("anonymousUser", null));

        Users result = usersService.getCurrentUser();

        assertNull(result);
    }

    @Test
    void getCurrentUser_returnsUserWhenAuthenticated() {
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(
                        "user@example.com",
                        null,
                        java.util.List.of(new SimpleGrantedAuthority("ROLE_USER"))));

        Users user = new Users();
        user.setEmail("user@example.com");
        when(usersRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        Users result = usersService.getCurrentUser();

        assertNotNull(result);
        assertEquals("user@example.com", result.getEmail());
    }

    @Test
    void addNew_createsRecruiterProfile() {
        UsersType type = new UsersType();
        type.setUserTypeId(1);
        Users user = new Users();
        user.setEmail("recruiter@example.com");
        user.setPassword("pass");
        user.setUserTypeId(type);

        when(usersRepository.findByEmail("recruiter@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass")).thenReturn("encoded");
        when(usersRepository.save(any(Users.class))).thenAnswer(i -> i.getArgument(0));

        Users saved = usersService.addNew(user, "Rick", "Smith", "ACME");

        assertNotNull(saved);
        assertTrue(saved.isActive());
        verify(recruiterProfileRepository).save(any(RecruiterProfile.class));
        verify(jobSeekerProfileRepository, never()).save(any());
    }

    @Test
    void addNew_throwsWhenUserTypeMissing() {
        Users user = new Users();
        user.setEmail("new@example.com");
        user.setPassword("pass");

        when(usersRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass")).thenReturn("encoded");
        when(usersRepository.save(any(Users.class))).thenAnswer(i -> i.getArgument(0));

        assertThrows(ValidationException.class, () -> usersService.addNew(user, "A", "B", "C"));
    }

    @Test
    void getJobSeekerProfileByUserId_returnsProfile() {
        JobSeekerProfile profile = new JobSeekerProfile();
        profile.setSkills(List.of());
        when(jobSeekerProfileRepository.findById(10)).thenReturn(Optional.of(profile));

        Optional<JobSeekerProfile> result = usersService.getJobSeekerProfileByUserId(10);

        assertTrue(result.isPresent());
        assertEquals(0, result.get().getSkills().size());
    }

    @Test
    void getRecruiterProfileByUserId_returnsProfile() {
        RecruiterProfile profile = new RecruiterProfile();
        when(recruiterProfileRepository.findById(11)).thenReturn(Optional.of(profile));

        Optional<RecruiterProfile> result = usersService.getRecruiterProfileByUserId(11);

        assertTrue(result.isPresent());
    }

    @Test
    void updateJobSeekerProfile_savesProfile() {
        JobSeekerProfile profile = new JobSeekerProfile();
        when(jobSeekerProfileRepository.save(profile)).thenReturn(profile);

        JobSeekerProfile result = usersService.updateJobSeekerProfile(profile);

        assertEquals(profile, result);
    }

    @Test
    void updateRecruiterProfile_savesProfile() {
        RecruiterProfile profile = new RecruiterProfile();
        when(recruiterProfileRepository.save(profile)).thenReturn(profile);

        RecruiterProfile result = usersService.updateRecruiterProfile(profile);

        assertEquals(profile, result);
    }

    @Test
    void getTotalUsersCount_returnsCount() {
        when(usersRepository.count()).thenReturn(7L);

        long result = usersService.getTotalUsersCount();

        assertEquals(7L, result);
    }

    @Test
    void getAllUsers_returnsList() {
        when(usersRepository.findAll()).thenReturn(List.of(new Users(), new Users()));

        List<Users> result = usersService.getAllUsers();

        assertEquals(2, result.size());
    }
}
