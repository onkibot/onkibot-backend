package com.onkibot.backend.models;

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
