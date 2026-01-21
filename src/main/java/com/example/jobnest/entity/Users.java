package com.example.jobnest.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @NotBlank(message = "Email boş ola bilməz")
    @Email(message = "Düzgün email formatı daxil edin")
    private String email;

    @NotBlank(message = "Şifrə boş ola bilməz")
    @Size(min = 6, message = "Şifrə ən azı 6 simvol olmalıdır")
    private String password;

    private boolean isActive;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date registrationDate;

    @NotNull(message = "İstifadəçi tipi seçilməlidir")
    @ManyToOne
    @JoinColumn(name = "user_type_id", referencedColumnName = "userTypeId")
    private UsersType userTypeId;

    @OneToOne(mappedBy = "userId", fetch = FetchType.LAZY)
    private RecruiterProfile recruiterProfile;

    @OneToOne(mappedBy = "userId", fetch = FetchType.LAZY)
    private JobSeekerProfile jobSeekerProfile;
}
