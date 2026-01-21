package com.example.jobnest.services;

public interface EmailService {
    void sendPasswordResetEmail(String to, String resetLink);
}
