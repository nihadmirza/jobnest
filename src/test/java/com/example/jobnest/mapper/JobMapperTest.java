package com.example.jobnest.mapper;

import com.example.jobnest.dto.request.JobCreateRequest;
import com.example.jobnest.dto.request.JobUpdateRequest;
import com.example.jobnest.dto.response.JobResponse;
import com.example.jobnest.entity.Job;
import com.example.jobnest.entity.RecruiterProfile;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JobMapperTest {

    private final JobMapper mapper = new JobMapper();

    @Test
    void toEntity_returnsNullForNullRequest() {
        assertNull(mapper.toEntity(null, new RecruiterProfile()));
    }

    @Test
    void toEntity_mapsFieldsAndDefaults() {
        JobCreateRequest request = new JobCreateRequest();
        request.setTitle("Dev");
        request.setDescription("Desc");
        request.setLocation("Remote");
        request.setCity("Baku");
        request.setState("Absheron");
        request.setCountry("AZ");
        request.setEmploymentType("Full-time");
        request.setSalary("1000");

        RecruiterProfile recruiter = new RecruiterProfile();
        recruiter.setUserAccountId(5);

        Job job = mapper.toEntity(request, recruiter);

        assertNotNull(job);
        assertEquals("Dev", job.getTitle());
        assertEquals("PENDING", job.getPaymentStatus());
        assertFalse(job.isActive());
        assertEquals(recruiter, job.getRecruiter());
    }

    @Test
    void updateEntity_skipsWhenNullsProvided() {
        mapper.updateEntity(null, new JobUpdateRequest());
        mapper.updateEntity(new Job(), null);
    }

    @Test
    void updateEntity_updatesFields() {
        Job job = new Job();
        JobUpdateRequest request = new JobUpdateRequest();
        request.setTitle("Updated");
        request.setDescription("New Desc");
        request.setLocation("Office");
        request.setCity("Ganja");
        request.setState("State");
        request.setCountry("AZ");
        request.setEmploymentType("Part-time");
        request.setSalary("900");
        request.setActive(true);

        mapper.updateEntity(job, request);

        assertEquals("Updated", job.getTitle());
        assertTrue(job.isActive());
    }

    @Test
    void toResponse_mapsRecruiterInfoWhenPresent() {
        RecruiterProfile recruiter = new RecruiterProfile();
        recruiter.setUserAccountId(10);
        recruiter.setFirstName("A");
        recruiter.setLastName("B");
        recruiter.setCompany("Co");

        Job job = new Job();
        job.setJobId(1);
        job.setTitle("Dev");
        job.setActive(true);
        job.setPaymentStatus("PENDING");
        job.setRecruiter(recruiter);

        JobResponse response = mapper.toResponse(job);

        assertNotNull(response);
        assertEquals(1, response.getJobId());
        assertEquals("Dev", response.getTitle());
        assertNotNull(response.getRecruiter());
        assertEquals(10, response.getRecruiter().getRecruiterId());
    }

    @Test
    void toResponse_returnsNullForNullJob() {
        assertNull(mapper.toResponse(null));
    }
}
