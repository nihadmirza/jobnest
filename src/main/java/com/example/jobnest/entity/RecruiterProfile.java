package com.example.jobnest.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    private String firstName;

    private String lastName;

    private String city;

    private String state;

    private String country;

    private String company;

    @Column(nullable = true , length = 64)
    private String profilePhoto;

    public RecruiterProfile(Users savedUser) {
        this.userId = savedUser;
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
                ", profilePhoto='" + profilePhoto + '\'' +
                '}';
    }
}
