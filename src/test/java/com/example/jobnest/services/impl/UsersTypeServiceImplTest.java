package com.example.jobnest.services.impl;

import com.example.jobnest.entity.UsersType;
import com.example.jobnest.repository.UsersTypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsersTypeServiceImplTest {

    @Mock
    private UsersTypeRepository usersTypeRepository;

    @InjectMocks
    private UsersTypeServiceImpl usersTypeService;

    @Test
    void getAll_returnsRepositoryData() {
        UsersType type = new UsersType();
        type.setUserTypeId(1);
        when(usersTypeRepository.findAll()).thenReturn(List.of(type));

        List<UsersType> result = usersTypeService.getAll();

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getUserTypeId());
    }
}
