package com.onkibot.backend.models;

public class SignupInfoModel extends CredentialsModel {
    private String name;
    private boolean isInstructor;

    public SignupInfoModel() {

    }

    public String getName() {
        return name;
    }

    public boolean getIsInstructor() {
        return isInstructor;
    }
}
