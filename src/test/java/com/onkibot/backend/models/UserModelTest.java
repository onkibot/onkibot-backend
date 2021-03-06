package com.onkibot.backend.models;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import org.junit.Before;
import org.junit.Test;

public class UserModelTest {
  private final int userId = 1;
  private final String name = "OnkiBOT Tester";
  private final Date createdTime = new Date(123);
  private final boolean isInstructor = false;

  private UserModel userModel;

  @Before
  public void setup() {
    this.userModel = new UserModel(userId, name, createdTime, isInstructor);
  }

  @Test
  public void testGetters() {
    assertEquals(userId, this.userModel.getUserId());
    assertEquals(name, this.userModel.getName());
    assertEquals(createdTime, this.userModel.getCreatedTime());
    assertEquals(isInstructor, this.userModel.getIsInstructor());
  }
}
