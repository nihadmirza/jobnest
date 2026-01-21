package com.example.jobnest.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "recruiter_profile")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecruiterProfile {

    @Id
    private int userAccountId;

    @OneToOne
    @JoinColumn(name = "user_account_id")
    @MapsId
    private Users userId;

    @Size(max = 100, message = "Ad 100 simvoldan uzun ola bilməz")
    private String firstName;

    @Size(max = 100, message = "Soyad 100 simvoldan uzun ola bilməz")
    private String lastName;

    @Size(max = 100, message = "Şəhər 100 simvoldan uzun ola bilməz")
    private String city;

    @Size(max = 100, message = "Ştat 100 simvoldan uzun ola bilməz")
    private String state;

    @Size(max = 100, message = "Ölkə 100 simvoldan uzun ola bilməz")
    private String country;

    @Size(max = 200, message = "Şirkət adı 200 simvoldan uzun ola bilməz")
    private String company;

    @org.hibernate.annotations.JdbcType(org.hibernate.type.descriptor.jdbc.VarbinaryJdbcType.class)
    @Column(name = "profile_photo") // Default column definition is sufficient for Hibernate 6
    private byte[] profilePhoto;

    @OneToMany(mappedBy = "recruiter", cascade = CascadeType.ALL)
    private List<Job> jobs;

    public RecruiterProfile(Users savedUser) {
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
        return "RecruiterProfile{" +
                "userAccountİd=" + userAccountId +
                ", userId=" + (userId != null ? userId.getUserId() : "null") +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", country='" + country + '\'' +
                ", company='" + company + '\'' +
                '}';
    }
}
