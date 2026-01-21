package com.example.jobnest.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "password_reset_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @OneToOne(targetEntity = Users.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id", referencedColumnName = "userId")
    private Users user;

    @Column(nullable = false)
    private Date expiryDate;

    public PasswordResetToken(Users user) {
        this.user = user;
        this.token = UUID.randomUUID().toString();
        // Token expires in 24 hours
        this.expiryDate = new Date(System.currentTimeMillis() + (24 * 60 * 60 * 1000));
    }

    public boolean isExpired() {
        return new Date().after(this.expiryDate);
    }
}
