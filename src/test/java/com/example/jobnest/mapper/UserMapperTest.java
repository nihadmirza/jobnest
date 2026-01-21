package com.example.jobnest.mapper;

import com.example.jobnest.dto.response.UserResponse;
import com.example.jobnest.entity.Users;
import com.example.jobnest.entity.UsersType;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserMapperTest {

    private final UserMapper mapper = new UserMapper();

    @Test
    void toResponse_returnsNullForNullUser() {
        assertNull(mapper.toResponse(null));
    }

    @Test
    void toResponse_mapsFieldsIncludingUserType() {
        UsersType type = new UsersType();
        type.setUserTypeId(2);
        type.setUserTypeName("Job Seeker");

        Users user = new Users();
        user.setUserId(7);
        user.setEmail("user@example.com");
        user.setActive(true);
        user.setRegistrationDate(new Date());
        user.setUserTypeId(type);

        UserResponse response = mapper.toResponse(user);

        assertEquals(7, response.getUserId());
        assertEquals("user@example.com", response.getEmail());
        assertEquals(true, response.getActive());
        assertEquals(2, response.getUserTypeId());
        assertEquals("Job Seeker", response.getUserTypeName());
    }
}
