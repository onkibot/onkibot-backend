package com.onkibot.backend.database.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

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

    protected User() { }

    public User(String email, String encodedPassword, String name, boolean isInstructor) {
        this.email = email;
        this.encodedPassword = encodedPassword;
        this.name = name;
        this.createdTime = new Date();
        this.isInstructor = isInstructor;
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
}
