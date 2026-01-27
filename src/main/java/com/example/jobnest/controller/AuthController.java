package com.example.jobnest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login(@RequestParam(required = false, defaultValue = "CANDIDATE") String role, Model model) {
        model.addAttribute("role", role);
        return "login";
    }
}
