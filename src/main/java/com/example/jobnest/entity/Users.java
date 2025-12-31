package com.example.jobnest.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class Users {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private int userId;

    @Column(unique = true)
    private String email;

    //NotEmpty
    private String password;

    private boolean isActive;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date registrationDate;

    @ManyToOne //(cascade = CascadeType.ALL) - type silmeye calismasin
    @JoinColumn(name = "user_type_id", referencedColumnName = "userTypeId")
    private UsersType userTypeId;
}
