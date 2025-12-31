package com.example.jobnest.services;

import com.example.jobnest.entity.Users;

import java.util.Optional;

public interface UsersService {

    Users addNew(Users users);

    Optional<Users> getUserByEmail(String email);
}
