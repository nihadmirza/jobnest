package com.example.jobnest.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "job_seeker_profile")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JobSeekerProfile {

    @Id
    private int userAccountId;

    @OneToOne
    @JoinColumn(name = "user_account_id")
    @MapsId
    private Users userId;

    @Size(max = 100, message = "First name cannot exceed 100 characters")
    private String firstName;

    @Size(max = 100, message = "Last name cannot exceed 100 characters")
    private String lastName;
    private String city;
    private String state;
    private String country;

    private String workAuthorization;

    private String employmentType;

    private String resume;

    @org.hibernate.annotations.JdbcType(org.hibernate.type.descriptor.jdbc.VarbinaryJdbcType.class)
    @Column(name = "profile_photo")
    private byte[] profilePhoto;

    @OneToMany(targetEntity = Skills.class, cascade = CascadeType.ALL, mappedBy = "jobSeekerProfile", fetch = FetchType.EAGER)
    private List<Skills> skills;

    public JobSeekerProfile(Users savedUser) {
        this.userId = savedUser;
    }

    @Transient
    public String getBase64Photo() {
        if (profilePhoto == null || profilePhoto.length == 0) {
            return null;
        }
        return java.util.Base64.getEncoder().encodeToString(profilePhoto);
    }

    @Transient
    public String getPhotosImagePath() {
        if (profilePhoto == null || profilePhoto.length == 0) {
            return "https://www.shutterstock.com/image-vector/vector-flat-illustration-gray-avatar-600nw-2264922221.jpg";
        }
        return "data:image/jpeg;base64," + java.util.Base64.getEncoder().encodeToString(profilePhoto);
    }

    @Override
    public String toString() {
        return "JobSeekerProfile{" +
                "userAccountId=" + userAccountId +
                ", userId=" + (userId != null ? userId.getUserId() : "null") +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", country='" + country + '\'' +
                ", workAuthorization='" + workAuthorization + '\'' +
                ", employmentType='" + employmentType + '\'' +
                ", resume='" + resume + '\'' +
                ", skillsCount=" + (skills != null ? skills.size() : 0) +
                '}';
    }
}
