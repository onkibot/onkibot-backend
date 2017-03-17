package com.onkibot.backend.database.entities;


import javax.persistence.*;

@Entity
public class Resource {
    @Id @GeneratedValue
    private Integer resourceId;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(nullable = false, length = 50)
    private String name;

    @Lob
    @Column(nullable = false)
    private String body;

    @ManyToOne
    @JoinColumn(name = "publisher_user_id")
    private User publisherUser;


    @OneToOne(mappedBy = "resource")
    private ExternalResource externalResource;

    protected Resource() {}

    public Resource(Category category, String name, String body, User publisherUser) {
        this.category = category;
        this.name = name;
        this.body = body;
        this.publisherUser = publisherUser;
    }

    public Integer getResourceId() {
        return resourceId;
    }

    public Category getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public String getBody() {
        return body;
    }

    public User getPublisherUser() {
        return publisherUser;
    }

    public void setPublisherUser(User publisherUser) {
        this.publisherUser = publisherUser;
    }

    public ExternalResource getExternalResource() {
        return externalResource;
    }

    public void setExternalResource(ExternalResource externalResource) {
        this.externalResource = externalResource;
    }
}
