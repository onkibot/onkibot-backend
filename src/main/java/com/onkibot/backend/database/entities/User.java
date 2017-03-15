package com.onkibot.backend.database.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class User implements Serializable {
    @Id @GeneratedValue
    private Integer userId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String encodedPassword;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Date createdTime;

    @Column(nullable = false)
    private boolean isInstructor;

    @ManyToMany
    @JoinTable(name = "attends")
    private List<Course> attending;

    @OneToMany(mappedBy = "publisherUser")
    private List<Resource> resources;

    protected User() { }

    public User(String email, String encodedPassword, String name, boolean isInstructor) {
        this.email = email;
        this.encodedPassword = encodedPassword;
        this.name = name;
        this.createdTime = new Date();
        this.isInstructor = isInstructor;
        this.attending = new ArrayList<>();
        this.resources = new ArrayList<>();
    }

    public Integer getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getEncodedPassword() {
        return encodedPassword;
    }

    public String getName() {
        return name;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public boolean getIsInstructor() {
        return isInstructor;
    }

    public List<Course> getAttending() {
        return attending;
    }

    public List<Resource> getResources() {
        return resources;
    }
}
