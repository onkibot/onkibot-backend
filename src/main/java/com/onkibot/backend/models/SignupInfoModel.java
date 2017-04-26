package com.onkibot.backend.models;

import com.onkibot.backend.database.entities.User;

/**
 * This class is used in the {@link com.onkibot.backend.api.SignupController}
 * when a visitor is attempting to sign up as a new {@link User}.
 */
public class SignupInfoModel extends CredentialsModel {
  private String name;
  private String usertype;

  protected SignupInfoModel() {}

  public SignupInfoModel(String email, String password, String name, String usertype) {
    super(email, password);
    this.name = name;
    this.usertype = usertype;
  }

  public String getName() {
    return name;
  }

  public String getUsertype() {
    return usertype;
  }

  public boolean getIsInstructor() {
    return "instructor".equals(usertype);
  }
}
