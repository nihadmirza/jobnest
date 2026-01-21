package com.example.jobnest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login(@RequestParam(required = false, defaultValue = "CANDIDATE") String role,
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String blocked,
            Model model) {
        if (blocked != null) {
            model.addAttribute("error", "Too many failed attempts. Account blocked for 15 minutes.");
        } else if (error != null) {
            model.addAttribute("error", "Invalid email or password.");
        }
        model.addAttribute("role", role);
        return "login";
    }
}
