package com.onkibot.backend.models;

public class CredentialsModel {
    private String email;
    private String password;

    protected CredentialsModel() { }

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
