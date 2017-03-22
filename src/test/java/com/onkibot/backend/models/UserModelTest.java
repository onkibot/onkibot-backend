package com.onkibot.backend.models;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class UserModelTest {
    private final int userId = 1;
    private final String email = "test@onkibot.com";
    private final String name = "OnkiBOT Tester";
    private final Date createdTime = new Date(123);
    private final boolean isInstructor = false;

    private UserModel userModel;

    @Before
    public void setup() {
        this.userModel = new UserModel(
                userId,
                email,
                name,
                createdTime,
                isInstructor
        );
    }

    @Test
    public void testGetters() {
        assertEquals(userId, this.userModel.getUserId());
        assertEquals(email, this.userModel.getEmail());
        assertEquals(name, this.userModel.getName());
        assertEquals(createdTime, this.userModel.getCreatedTime());
        assertEquals(isInstructor, this.userModel.getIsInstructor());
    }
}
