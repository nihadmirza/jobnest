package com.example.jobnest.controller;

import com.example.jobnest.services.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminUsersController {

    private final UsersService usersService;

    @GetMapping("/users")
    public String listAllUsers(Model model) {
        model.addAttribute("users", usersService.getAllUsers());
        return "admin-users";
    }
}
