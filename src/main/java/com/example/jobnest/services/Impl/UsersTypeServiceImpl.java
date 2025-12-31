package com.example.jobnest.services.Impl;

import com.example.jobnest.entity.UsersType;
import com.example.jobnest.repository.UsersTypeRepository;
import com.example.jobnest.services.UsersTypeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsersTypeServiceImpl implements UsersTypeService {

    private final UsersTypeRepository usersTypeRepository;

    public UsersTypeServiceImpl(UsersTypeRepository usersTypeRepository) {
        this.usersTypeRepository = usersTypeRepository;
    }

    public List<UsersType> getAll() {
        return usersTypeRepository.findAll();
    }
}
