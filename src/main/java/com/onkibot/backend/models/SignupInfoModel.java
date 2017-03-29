package com.onkibot.backend.models;

public class SignupInfoModel extends CredentialsModel {
  private String name;
  private boolean isInstructor;

  protected SignupInfoModel() {}

  public SignupInfoModel(String email, String password, String name, boolean isInstructor) {
    super(email, password);
    this.name = name;
    this.isInstructor = isInstructor;
  }

  public String getName() {
    return name;
  }

  public boolean getIsInstructor() {
    return isInstructor;
  }
}
