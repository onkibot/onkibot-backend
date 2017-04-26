package com.onkibot.backend.models;

import com.onkibot.backend.database.entities.User;

/**
 * This class is used in the {@link com.onkibot.backend.api.SessionController}
 * when a {@link User} is attempting to login.
 */
public class CredentialsModel {
  private String email;
  private String password;

  protected CredentialsModel() {}

  public CredentialsModel(String email, String password) {
    this.email = email;
    this.password = password;
  }

  public String getEmail() {
    return email;
  }

  public String getPassword() {
    return password;
  }
}
