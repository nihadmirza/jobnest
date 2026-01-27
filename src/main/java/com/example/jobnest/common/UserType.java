package com.example.jobnest.common;

import com.example.jobnest.entity.UsersType;

import java.util.Optional;


public enum UserType {
    RECRUITER(1),
    JOB_SEEKER(2),
    ADMIN(3);

    private final int id;

    UserType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static Optional<UserType> fromId(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        for (UserType type : values()) {
            if (type.id == id) {
                return Optional.of(type);
            }
        }
        return Optional.empty();
    }

    public static Optional<UserType> fromUsersType(UsersType usersType) {
        if (usersType == null) {
            return Optional.empty();
        }
        return fromId(usersType.getUserTypeId());
    }
}

