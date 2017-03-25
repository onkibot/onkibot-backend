package com.onkibot.backend.models;

public class ResourceInputModel {
    private String name;
    private String body;

    protected ResourceInputModel() { }

    public ResourceInputModel(String name, String body) {
        this.name = name;
        this.body = body;
    }

    public String getName() {
        return name;
    }

    public String getBody() {
        return body;
    }
}
