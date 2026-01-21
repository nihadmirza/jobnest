package com.example.jobnest.controller;

import com.example.jobnest.entity.Users;
import com.example.jobnest.services.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminUsersController {

    private final UsersService usersService;

    @GetMapping("/users")
    public String listAllUsers(Model model) {
        try {
            List<Users> users = usersService.getAllUsers();
            model.addAttribute("users", users);
            return "admin-users";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading users: " + e.getMessage());
            return "error";
        }
    }
}
